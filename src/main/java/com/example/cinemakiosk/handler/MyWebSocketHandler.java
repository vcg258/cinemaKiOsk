package com.example.cinemakiosk.handler;

import com.example.cinemakiosk.dto.SeatSocketDTO;
import com.example.cinemakiosk.service.ReservationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.net.URI;
import java.util.*;
import java.util.concurrent.*;


@Log4j2
@Component
@RequiredArgsConstructor
public class MyWebSocketHandler extends TextWebSocketHandler {
    private final ObjectMapper objectMapper;

    private static final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
    // Key: "scheduleId", Value: "{userId:uuid / scheduleId:key와 동일 / 좌석 List<String> / action : "RESERVE" }"
    private static final Map<Long, List<SeatSocketDTO>> seatOccupancyMap = new ConcurrentHashMap<>();
    // 지연 삭제를 위한 스케줄러와 관리 맵
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final Map<String, ScheduledFuture<?>> removalTasks = new ConcurrentHashMap<>();

    // 첫번째 접속을 했을 때 실행할 로직
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String query = session.getUri().getQuery();
        String userId = null;
        String page = null;
        String scheduleId = null;

        // 1. 파라미터 추출 (userId 포함)
        if (query != null && query.contains("userId=") && query.contains("page=")) {
            String[] splits = query.split("&");
            userId = splits[0].split("userId=")[1];
            page = splits[1].split("page=")[1];
            if (splits.length > 2 && splits[2].contains("scheduleId=")){
                scheduleId = splits[2].split("scheduleId=")[1];
            }
        }

        if (userId == null || page == null) {
            session.close(CloseStatus.BAD_DATA);
            return;
        }

        // 2. 재접속 확인: 만약 삭제 대기 중인 작업이 있다면 취소
        ScheduledFuture<?> scheduledTask = removalTasks.remove(userId);
        if (scheduledTask != null) {
            scheduledTask.cancel(false);
            log.info("사용자 [{}] 재접속 확인 - 좌석 삭제 작업 취소", userId);
        }

        // 3. 세션에 추가 내용을 등록
        session.getAttributes().put("userId", userId);
        sessions.add(session);
        log.info("새 연결: userId={}", userId);

    }


    // 웹에서 send를 받았을 때 처리하는 메소드
    @Override
    protected void handleTextMessage(WebSocketSession session, @NonNull TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("받은 데이터 : {}", payload);
        SeatSocketDTO response = objectMapper.readValue(payload, SeatSocketDTO.class);
        String userId = (String) session.getAttributes().get("userId");
        Long scheduleId = response.getScheduleId();

        // 스케줄별 맵이 없으면 생성
        seatOccupancyMap.putIfAbsent(scheduleId, new CopyOnWriteArrayList<>());
        List<SeatSocketDTO> occupancyList = seatOccupancyMap.get(scheduleId);

        switch (response.getAction().toUpperCase()) {
            case "GET" -> {
                // 1. 이 사용자가 이전에 'RESERVE' 해둔 데이터가 있는지 확인
                Optional<SeatSocketDTO> myPreviousData = occupancyList.stream()
                        .filter(dto -> dto.getUserId().equals(userId))
                        .findFirst();

                if (myPreviousData.isPresent()) {
                    // [복구 로직] 본인에게 전송할 응답에 이전 좌석 정보를 담아 보냄
                    // 전송 시 action을 "INIT_SELECTION"으로 보내 프론트에서 구별하게 함
                    SeatSocketDTO recoveryData = myPreviousData.get();
                    recoveryData.setAction("INIT_SELECTION");
                    session.sendMessage(new TextMessage(objectMapper.writeValueAsString(recoveryData)));

                    log.info("사용자 [{}] 기존 좌석 복구 완료: {}", userId, recoveryData.getSeats());
                }

                // 2. 현재 스케줄의 전체 점유 현황을 본인에게 전송 (남이 잡은 자리 확인용)
                broadcastStatus(session, scheduleId);
            }
            case "RESERVE" -> {
                // 해당 유저의 기존 점유 정보가 있다면 업데이트, 없으면 추가
                occupancyList.removeIf(dto -> dto.getUserId().equals(userId));
                occupancyList.add(new SeatSocketDTO(userId, scheduleId, response.getSeats(), "RESERVE"));

                broadcastStatus(session, scheduleId);
            }
            case "RELEASE" -> {
                // 해당 유저의 점유 정보 삭제
                occupancyList.removeIf(dto -> dto.getUserId().equals(userId));

                broadcastStatus(session, scheduleId);
            }
        }
    }

    // 연결이 종료되고나서 실행하는 메서드
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String userId = (String) session.getAttributes().get("userId");
        sessions.remove(session);

        if (userId != null) {
            log.info("사용자 [{}] 연결 종료 - 10초 후 좌석 해제 예약", userId);

            ScheduledFuture<?> task = scheduler.schedule(() -> {
                seatOccupancyMap.forEach((scheduleId, list) -> {
                    boolean removed = list.removeIf(dto -> dto.getUserId().equals(userId));
                    if (removed) {
                        try {
                            log.info("사용자 [{}]의 좌석 자동 해제 (스케줄: {})", userId, scheduleId);
                            broadcastStatus(session, scheduleId);
                        } catch (Exception e) {
                            log.error("브로드캐스트 오류", e);
                        }
                    }
                });
                removalTasks.remove(userId);
            }, 5, TimeUnit.MINUTES);

            removalTasks.put(userId, task);
        }
    }

    /**
     * 해당 스케줄을 보고 있는 모든 유저에게 현재 점유 리스트 전송
     */
    private void broadcastStatus(WebSocketSession session, Long scheduleId) throws Exception {
        List<String> currentStatus = extractDTO(scheduleId);
        // JSON 구조 개선
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("action", "UPDATE_OCCUPANCY"); // 하드코딩된 "RESERVE" 대신 전용 액션명 사용 권장
        responseMap.put("scheduleId", scheduleId);
        responseMap.put("seats", currentStatus);
        responseMap.put("userId", session.getAttributes().get("userId")); // 누가 변경했는지 정보

        String jsonResponse = objectMapper.writeValueAsString(responseMap);
        TextMessage message = new TextMessage(jsonResponse);

        for (WebSocketSession s : sessions) {
            if (s.isOpen()) {
                s.sendMessage(message);
            }
        }
    }

    /**
     * 특정 스케쥴에 대한 예약된 좌석을 선별하는 메서드
     **/
    private List<String> extractDTO(Long scheduleId) {
        List<SeatSocketDTO> currentStatus = seatOccupancyMap.getOrDefault(scheduleId, new ArrayList<>());
        List<String> result = new ArrayList<>();

        for (SeatSocketDTO seatSocketDTO : currentStatus) {
            result.addAll(seatSocketDTO.getSeats());
        }

        return result;
    }
}