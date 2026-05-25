package com.example.cinemakiosk.handler;

import com.example.cinemakiosk.dto.SeatSocketDTO;
import com.example.cinemakiosk.service.SeatOccupancyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Optional;


@Log4j2
@Component
@RequiredArgsConstructor
public class MyWebSocketHandler extends TextWebSocketHandler {
    private final ObjectMapper objectMapper;
    private final SeatOccupancyService seatOccupancyService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String query = session.getUri().getQuery();
        String userId = null;
        String page = null;
        Long scheduleId = null;

        if (query != null && query.contains("userId=") && query.contains("page=")) {
            String[] splits = query.split("&");
            userId = splits[0].split("userId=")[1];
            page = splits[1].split("page=")[1];
            if (splits.length > 2 && splits[2].contains("scheduleId=")) {
                scheduleId = Long.parseLong(splits[2].split("scheduleId=")[1]);
            }
        }

        if (userId == null || page == null) {
            session.close(CloseStatus.BAD_DATA);
            return;
        }

        seatOccupancyService.cancelScheduledRelease(userId);

        session.getAttributes().put("userId", userId);
        if (scheduleId != null) {
            session.getAttributes().put("scheduleId", scheduleId);
        }
        seatOccupancyService.registerSession(session);
        log.info("새 연결: userId={}, scheduleId={}", userId, scheduleId);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, @NonNull TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("받은 데이터 : {}", payload);
        SeatSocketDTO response = objectMapper.readValue(payload, SeatSocketDTO.class);
        String userId = (String) session.getAttributes().get("userId");
        Long scheduleId = response.getScheduleId();

        switch (response.getAction().toUpperCase()) {
            case "GET" -> {
                Optional<SeatSocketDTO> myPreviousData =
                        seatOccupancyService.findUserOccupancy(scheduleId, userId);

                if (myPreviousData.isPresent()) {
                    seatOccupancyService.sendInitSelection(session, myPreviousData.get());
                }

                seatOccupancyService.sendOccupancyToSession(session, scheduleId, userId);
            }
            case "RESERVE" -> seatOccupancyService.reserve(userId, scheduleId, response.getSeats());
            case "RELEASE" -> seatOccupancyService.releaseFromSocket(userId, scheduleId);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String userId = (String) session.getAttributes().get("userId");
        seatOccupancyService.unregisterSession(session);

        if (userId != null) {
            seatOccupancyService.scheduleReleaseOnDisconnect(userId);
        }
    }
}
