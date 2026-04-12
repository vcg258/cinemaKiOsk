package com.example.cinemakiosk.filter;

import com.example.cinemakiosk.exception.RefreshTokenException;
import com.example.cinemakiosk.exception.enums.ErrorCase;
import com.example.cinemakiosk.util.JwtUtil;
import com.google.gson.Gson;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Log4j2
@RequiredArgsConstructor
public class RefreshTokenFilter extends OncePerRequestFilter {
    private final String refreshPath;
    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();

        // 요청경로가 refreshPath가 아님 스킵
        if (path.startsWith(refreshPath)) {
            log.info("skip Refreshing token...");
            filterChain.doFilter(request, response);
            return;
        }

        log.info("Refreshing token...");
        // 요청온 JSON -> Map
        Map<String, String> tokens = parseRequestJSON(request);

        String accessToken = tokens.get("accessToken");
        String refreshToken = tokens.get("refreshToken");

        log.info("accessToken: {}", accessToken);
        log.info("refreshToken: {}", refreshToken);

        // AccessToken 체크
        try {
            checkAccessToken(accessToken);
        } catch (RefreshTokenException refreshTokenException) {
            refreshTokenException.sendResponseError(response);
            return;
        }

        // RefreshToken 체크
        Map<String, Object> refreshClaims = null;
        try {
            refreshClaims = checkRefreshToken(refreshToken);
            log.info(refreshClaims);
        } catch (RefreshTokenException refreshTokenException) {
            refreshTokenException.sendResponseError(response);
            return;
        }

        Long exp = (Long) refreshClaims.get("exp"); // Refresh토큰 안에 들어있는 만료시간
        Date expTime = new Date(Instant.ofEpochMilli(exp).toEpochMilli() * 1000); // JWT의 만료날짜를 Date객체로 변환
        Date current = new Date(System.currentTimeMillis()); // 현재시간

        // 만료시간이 얼마나 남았는지 알기위해 선언
        long gapTime = (expTime.getTime() - current.getTime());
        log.info("-------------------------------------------");
        log.info("current: {}", current);
        log.info("expTime: {}", expTime);
        log.info("gap: {}", gapTime);

        // AccessToken 재발급 할 아이디 지정
        String adminId = (String) refreshClaims.get("adminId");

        String accessTokenValue = jwtUtil.generateToken(Map.of("adminId", adminId), 1);
        String refreshTokenValue = tokens.get("refreshToken");

        // RefreshToken이 3일도 안남았으면 재발급
        if (gapTime < (1000* 60* 60* 24 * 3)) {
            log.info("new Refresh Token required... ");
            refreshTokenValue = jwtUtil.generateToken(Map.of("adminId", adminId), 30);
        }

        log.info("Refresh Token result....................");
        log.info("accessToken: {}", accessTokenValue);
        log.info("refreshToken: {}", refreshTokenValue);

        sendTokens(accessTokenValue, refreshTokenValue, response);
    }

    /**
     * AccessToken 만료기간 체크 헬퍼 메서드
     * @param accessToken 엑세스 토큰
     * @throws RefreshTokenException 엑세스 토큰 없음
     */
    private void checkAccessToken(String accessToken) throws RefreshTokenException {
        try {
            jwtUtil.validateToken(accessToken);
        } catch (ExpiredJwtException expiredJwtException) {
            log.info("Access Token has expired");
        } catch (Exception e) {
            throw new RefreshTokenException(ErrorCase.NO_ACCESS);
        }
    }

    /**
     * RefreshToken이 유효한지 만료되지 않았는지를 확인하는 헬퍼 메서드
     * @param refreshToken RefreshToken
     * @return 유효한 RefreshToken
     * @throws RefreshTokenException 만료거나 토큰 없음
     */
    private Map<String, Object> checkRefreshToken(String refreshToken) throws RefreshTokenException {
        try {
            Map<String, Object> values = jwtUtil.validateToken(refreshToken);
            return values;
        } catch (ExpiredJwtException expiredJwtException) {
            throw new RefreshTokenException(ErrorCase.OLD_REFRESH);
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RefreshTokenException(ErrorCase.NO_REFRESH);
        }
    }

    /**
     * Gson라이브러리 이용한 JSON -> Java(Map) 변환 메서드
     * @param request 요청값
     * @return Map
     */
    private Map<String, String> parseRequestJSON(HttpServletRequest request) {
        try (Reader reader = new InputStreamReader(request.getInputStream())) {
            Gson gson = new Gson();
            return gson.fromJson(reader, Map.class);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return null;
    }

    /**
     * 새로발급한 AccessToken과 RefreshToken을 JSON으로 새로 전달
     * @param accessTokenValue 재발급 AccessToken
     * @param refreshTokenValue 재발급 RefreshToken
     * @param response JSON으로 묶어서 전달
     */
    private void sendTokens(String accessTokenValue, String refreshTokenValue, HttpServletResponse response) {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Gson gson = new Gson();
        String jsonStr = gson.toJson(Map.of("accessToken", accessTokenValue, "refreshToken", refreshTokenValue));

        try {
            response.getWriter().println(jsonStr);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
