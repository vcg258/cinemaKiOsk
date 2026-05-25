package com.example.cinemakiosk.service;

import com.example.cinemakiosk.dto.SeatSocketDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.*;
import java.util.concurrent.*;

/**
 * WebSocket 임시 좌석 점유 상태를 관리한다.
 * MyWebSocketHandler와 REST 해제 API가 공유한다.
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class SeatOccupancyService {

    /**
     * 연결 종료 후 점유 유지 시간(안전 장치).
     * 탭/창 닫기·결제 이동 등 WS만 끊긴 경우 적용. 로고·홈·타이머·뒤로가기는 즉시 RELEASE.
     */
    public static final long DISCONNECT_HOLD_MINUTES = 5;

    private final ObjectMapper objectMapper;

    private final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
    private final Map<Long, List<SeatSocketDTO>> seatOccupancyMap = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final Map<String, ScheduledFuture<?>> removalTasks = new ConcurrentHashMap<>();

    public void registerSession(WebSocketSession session) {
        sessions.add(session);
    }

    public void unregisterSession(WebSocketSession session) {
        sessions.remove(session);
    }

    /** 재접속 시 예약된 지연 해제 작업 취소 */
    public void cancelScheduledRelease(String userId) {
        ScheduledFuture<?> scheduledTask = removalTasks.remove(userId);
        if (scheduledTask != null) {
            scheduledTask.cancel(false);
            log.info("사용자 [{}] 재접속 — 좌석 지연 해제 취소", userId);
        }
    }

    public Optional<SeatSocketDTO> findUserOccupancy(Long scheduleId, String userId) {
        List<SeatSocketDTO> list = seatOccupancyMap.getOrDefault(scheduleId, List.of());
        return list.stream().filter(dto -> dto.getUserId().equals(userId)).findFirst();
    }

    public void reserve(String userId, Long scheduleId, List<String> seats) throws Exception {
        List<SeatSocketDTO> occupancyList = getOrCreateList(scheduleId);
        occupancyList.removeIf(dto -> dto.getUserId().equals(userId));
        occupancyList.add(new SeatSocketDTO(userId, scheduleId, seats, "RESERVE"));
        broadcastStatus(scheduleId, userId, null);
    }

    /** 즉시 점유 해제 (뒤로가기·REST API) */
    public boolean releaseImmediately(String userId, Long scheduleId) throws Exception {
        cancelScheduledRelease(userId);
        List<SeatSocketDTO> occupancyList = seatOccupancyMap.get(scheduleId);
        if (occupancyList == null) {
            return false;
        }
        boolean removed = occupancyList.removeIf(dto -> dto.getUserId().equals(userId));
        if (removed) {
            log.info("사용자 [{}] 좌석 즉시 해제 (스케줄: {})", userId, scheduleId);
            broadcastStatus(scheduleId, userId, null);
        }
        return removed;
    }

    /** WebSocket RELEASE 메시지 처리 */
    public void releaseFromSocket(String userId, Long scheduleId) throws Exception {
        releaseImmediately(userId, scheduleId);
    }

    public List<String> extractAllSeats(Long scheduleId) {
        List<SeatSocketDTO> currentStatus = seatOccupancyMap.getOrDefault(scheduleId, new ArrayList<>());
        List<String> result = new ArrayList<>();
        for (SeatSocketDTO seatSocketDTO : currentStatus) {
            result.addAll(seatSocketDTO.getSeats());
        }
        return result;
    }

    public void sendInitSelection(WebSocketSession session, SeatSocketDTO stored) throws Exception {
        SeatSocketDTO recovery = new SeatSocketDTO(
                stored.getUserId(),
                stored.getScheduleId(),
                new ArrayList<>(stored.getSeats()),
                "INIT_SELECTION"
        );
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(recovery)));
        log.info("사용자 [{}] 기존 좌석 복구: {}", stored.getUserId(), stored.getSeats());
    }

    public void broadcastOccupancy(Long scheduleId, String actorUserId) throws Exception {
        broadcastStatus(scheduleId, actorUserId, null);
    }

    /** GET 직후 요청 세션에만 점유 현황 전송 (재접속 클라이언트 INIT/UPDATE 순서 보장) */
    public void sendOccupancyToSession(WebSocketSession session, Long scheduleId, String actorUserId) throws Exception {
        broadcastStatus(scheduleId, actorUserId, session);
    }

    /**
     * 연결 종료 시 지연 해제 예약.
     * 이미 RELEASE로 해제된 경우 occupancyList에 없으므로 지연 작업은 no-op.
     */
    public void scheduleReleaseOnDisconnect(String userId) {
        cancelScheduledRelease(userId);
        log.info("사용자 [{}] 연결 종료 — {}분 후 미복구 시 좌석 해제 예약", userId, DISCONNECT_HOLD_MINUTES);

        ScheduledFuture<?> task = scheduler.schedule(() -> {
            seatOccupancyMap.forEach((scheduleId, list) -> {
                boolean removed = list.removeIf(dto -> dto.getUserId().equals(userId));
                if (removed) {
                    try {
                        log.info("사용자 [{}] 좌석 자동 해제 (스케줄: {})", userId, scheduleId);
                        broadcastStatus(scheduleId, userId, null);
                    } catch (Exception e) {
                        log.error("브로드캐스트 오류", e);
                    }
                }
            });
            removalTasks.remove(userId);
        }, DISCONNECT_HOLD_MINUTES, TimeUnit.MINUTES);

        removalTasks.put(userId, task);
    }

    private List<SeatSocketDTO> getOrCreateList(Long scheduleId) {
        seatOccupancyMap.putIfAbsent(scheduleId, new CopyOnWriteArrayList<>());
        return seatOccupancyMap.get(scheduleId);
    }

    private void broadcastStatus(Long scheduleId, String actorUserId, WebSocketSession onlySession) throws Exception {
        List<String> currentStatus = extractAllSeats(scheduleId);
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("action", "UPDATE_OCCUPANCY");
        responseMap.put("scheduleId", scheduleId);
        responseMap.put("seats", currentStatus);
        responseMap.put("userId", actorUserId);

        String jsonResponse = objectMapper.writeValueAsString(responseMap);
        TextMessage message = new TextMessage(jsonResponse);

        if (onlySession != null && onlySession.isOpen()) {
            onlySession.sendMessage(message);
            return;
        }

        for (WebSocketSession s : sessions) {
            if (!s.isOpen()) {
                continue;
            }
            Object watching = s.getAttributes().get("scheduleId");
            if (watching instanceof Long sid && sid.equals(scheduleId)) {
                s.sendMessage(message);
            }
        }
    }
}
