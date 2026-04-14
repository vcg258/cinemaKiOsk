package com.example.cinemakiosk.exception;

import com.example.cinemakiosk.exception.enums.ErrorCase;
import com.google.gson.Gson;
import com.solapi.shadow.okhttp3.internal.http2.ErrorCode;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

@Log4j2
public class RefreshTokenException extends RuntimeException {
    private final ErrorCase errorCase;

    public RefreshTokenException(ErrorCase errorCase) {
        super(errorCase.name());
        this.errorCase = errorCase;
    }

    /**
     * RefreshToken의 예외를 JSON으로 전달
     * @param response 예외 JSON
     */
    public void sendResponseError(HttpServletResponse response) {

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Gson gson = new Gson();
        String responseStr = gson.toJson(Map.of("msg", errorCase.name(), "time", new Date()));
        log.info("RefreshToken Exception: {}", responseStr);

        try {
            response.getWriter().println(responseStr);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
