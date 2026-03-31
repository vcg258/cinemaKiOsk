package com.example.cinemakiosk.handler;

import com.example.cinemakiosk.dto.ReservationSeatDTO;
import com.example.cinemakiosk.dto.SeatSocketDTO;
import com.example.cinemakiosk.service.ReservationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.net.URI;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Log4j2
@Component
@RequiredArgsConstructor
public class MyWebSocketHanler extends TextWebSocketHandler {
    private final ObjectMapper objectMapper;
    private final ReservationService reservationService;

    private static final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
    // Key: "scheduleId:seatNumber", Value: "userId(UUID)"
    private static final Map<String, String> seatOccupancyMap = new ConcurrentHashMap<>();

    // 지연 삭제를 위한 스케줄러와 관리 맵
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final Map<String, ScheduledFuture<?>> removalTasks = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        URI uri = session.getUri();
        String query = uri.getQuery();

        Long scheduleId = null;
        String userId = null;

        // 1. 파라미터 추출 (userId 포함)
        if (query != null) {
            String[] params = query.split("&");
            for (String param : params) {
                String[] pair = param.split("=");
                if (pair.length < 2) continue;
                if (pair[0].equals("scheduleId")) scheduleId = Long.parseLong(pair[1]);
                if (pair[0].equals("userId")) userId = pair[1];
            }
        }

        if (scheduleId == null || userId == null) {
            session.close(CloseStatus.BAD_DATA);
            return;
        }

        // 2. 재접속 확인: 만약 삭제 대기 중인 작업이 있다면 취소
        ScheduledFuture<?> scheduledTask = removalTasks.remove(userId);
        if (scheduledTask != null) {
            scheduledTask.cancel(false);
            log.info("사용자 [{}] 재접속 확인 - 좌석 삭제 작업 취소", userId);
        }

        session.getAttributes().put("scheduleId", scheduleId);
        session.getAttributes().put("userId", userId);
        sessions.add(session);

        // 3. INIT_STATE 전송 시 누가 잡고 있는지 정보 포함 (좌석번호:사용자ID)
        sendInitialState(session, scheduleId, userId);
    }

    private void sendInitialState(WebSocketSession session, Long scheduleId, String myId) throws Exception {
        Map<String, Object> initData = new HashMap<>();
        initData.put("type", "INIT_STATE");
        initData.put("reserved", reservationService.readAllSeatByScheduleId(scheduleId));

        List<String> occupiedInfo = seatOccupancyMap.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(scheduleId + ":"))
                .map(entry -> entry.getKey() + ":" + entry.getValue()) // "1:A1:uuid"
                .collect(Collectors.toList());

        initData.put("occupied", occupiedInfo);
        initData.put("myId", myId);
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(initData)));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        SeatSocketDTO request = objectMapper.readValue(message.getPayload(), SeatSocketDTO.class);
        Long scheduleId = request.getScheduleId();
        String userId = (String) session.getAttributes().get("userId");

        List<String> occupiedList = new ArrayList<>();
        List<String> releasedList = new ArrayList<>();

        for (ReservationSeatDTO seat : request.getSeats()) {
            String occupancyKey = scheduleId + ":" + seat.getSeatNumber();

            if (!seatOccupancyMap.containsKey(occupancyKey)) {
                seatOccupancyMap.put(occupancyKey, userId);
                occupiedList.add(occupancyKey + ":" + userId);
            } else if (seatOccupancyMap.get(occupancyKey).equals(userId)) {
                seatOccupancyMap.remove(occupancyKey);
                releasedList.add(occupancyKey + ":" + userId);
            }
        }

        broadcastStatus("OCCUPIED", occupiedList, userId, scheduleId);
        broadcastStatus("RELEASED", releasedList, userId, scheduleId);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String userId = (String) session.getAttributes().get("userId");
        Long scheduleId = (Long) session.getAttributes().get("scheduleId");
        sessions.remove(session);

        if (userId != null && scheduleId != null) {
            log.info("사용자 [{}] 연결 종료 - 10초 후 좌석 삭제 예약", userId);

            // 10초 뒤 실행될 작업 예약
            ScheduledFuture<?> task = scheduler.schedule(() -> {
                List<String> releasedSeats = new ArrayList<>();
                seatOccupancyMap.entrySet().removeIf(entry -> {
                    if (entry.getValue().equals(userId)) {
                        releasedSeats.add(entry.getKey() + ":" + userId);
                        return true;
                    }
                    return false;
                });

                removalTasks.remove(userId);

                if (!releasedSeats.isEmpty()) {
                    try {
                        broadcastStatus("RELEASED", releasedSeats, "SYSTEM", scheduleId);
                        log.info("사용자 [{}]의 좌석이 10초 초과로 인해 자동 해제됨", userId);
                    } catch (Exception e) {
                        log.error("브로드캐스트 오류", e);
                    }
                }
            }, 10, TimeUnit.SECONDS);

            removalTasks.put(userId, task);
        }
    }

    private void broadcastStatus(String type, List<String> seatKeys, String senderId, Long scheduleId) throws Exception {
        if (seatKeys.isEmpty()) return;
        Map<String, Object> response = new HashMap<>();
        response.put("type", type);
        response.put("seats", seatKeys);
        response.put("actionBy", senderId);

        TextMessage message = new TextMessage(objectMapper.writeValueAsString(response));
        for (WebSocketSession s : sessions) {
            Long sId = (Long) s.getAttributes().get("scheduleId");
            if (s.isOpen() && scheduleId.equals(sId)) {
                s.sendMessage(message);
            }
        }
    }
}