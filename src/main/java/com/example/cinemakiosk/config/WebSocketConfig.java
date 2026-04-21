package com.example.cinemakiosk.config;

import com.example.cinemakiosk.handler.MyWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * 웹소켓의 설정을 관리하는 클래스
 */
@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    //실질적으로 메세지를 처리할 핸들러 객체를 bean으로 주입받기.
    private final MyWebSocketHandler myWebSocketHandler;


    /**
     * 웹소켓 핸들러를 등록하는 메서드
     * @param registry : 핸들러를 등록하고 경로를 지정할 수 있는 인수.
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        System.out.println("==== WebSocket Handler Registered on /ws/seats ====");
        // 1. "ws://서버주소/ws/seats" 경로로 들어오는 연결을 myWebSocketHandler가 처리하도록 매핑합니다.
        registry.addHandler(myWebSocketHandler, "/ws/seats")
                .setAllowedOrigins("*");       // 모든 도메인 허용
    }
}
