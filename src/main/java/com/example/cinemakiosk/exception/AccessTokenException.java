package com.example.cinemakiosk.exception;

import com.example.cinemakiosk.exception.enums.TokenError;
import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

public class AccessTokenException extends RuntimeException {
    private final TokenError tokenError; // 엑세스 토큰 enum

    // 생성자
    public AccessTokenException(TokenError tokenError) {
        super(tokenError.name());
        this.tokenError = tokenError;
    }

    /**
     * 엑세스 토큰 예외를 JSON으로 반환
     * @param response 예외 JSON
     */
    public void sendResponseError(HttpServletResponse response) {
        response.setStatus(tokenError.getStatus());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Gson gson = new Gson();
        String responseStr = gson.toJson(Map.of("msg", tokenError.getMsg(), "time", new Date()));

        try {
            response.getWriter().println(responseStr);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
