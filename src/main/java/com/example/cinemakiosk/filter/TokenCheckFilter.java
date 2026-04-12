package com.example.cinemakiosk.filter;

import com.example.cinemakiosk.exception.AccessTokenException;
import com.example.cinemakiosk.exception.enums.TokenError;
import com.example.cinemakiosk.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

@Log4j2
@RequiredArgsConstructor
public class TokenCheckFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 특정 요청 경로에만 JWT 검사
        // 클라이언트가 요청한 경로
        String path = request.getRequestURI();

        // 시작경로가 /api/admin/ 아닐 경우 Pass
        if (!path.startsWith("/api/admin/")) {
            filterChain.doFilter(request, response);
            return;
        }

        log.info("doFilterInternal...");
        log.info(jwtUtil);


        // 유효한 AccessToken이 맞다면 통과 아니면 예외
        try {
            validateAccessToken(request);
            filterChain.doFilter(request, response);
        } catch (AccessTokenException accessTokenException) {
            accessTokenException.sendResponseError(response);
        }

        // TODO filterChain.doFilter(request, response);

    }

    // AccessToken 검증
    private Map<String, Object> validateAccessToken(HttpServletRequest request) {
        String headerStr = request.getHeader("Authorization");

        // 헤더 없거나 8자 미만이다 401
        if (headerStr == null || headerStr.length() < 8) {
            throw new AccessTokenException(TokenError.UNACCEPT);
        }
        String tokenType = headerStr.substring(0, 6); // 해더 추출
        String tokenStr = headerStr.substring(7); // 해더 제외

        // 토큰타입이 Bearer가 아니다 401
        if (!tokenType.equals("Bearer")) {
            throw new AccessTokenException(TokenError.BADTYPE);
        }
        try {
            // 토큰 정상적이다 반환
            return jwtUtil.validateToken(tokenStr);
        } catch (MalformedJwtException malformedJwtException) {
            // 형식이 잘못됨 403
            log.error("MalformedJwtException : {}", malformedJwtException.getMessage());
            throw new AccessTokenException(TokenError.MALFORM);
        } catch (SignatureException signatureException) {
            // 서명이 위조됨 403
            log.error("SignatureException : {}", signatureException.getMessage());
            throw new AccessTokenException(TokenError.BADSIGN);
        } catch (ExpiredJwtException expiredJwtException) {
            // 만료 403
            log.error("ExpiredJwtException : {}", expiredJwtException.getMessage());
            throw new AccessTokenException(TokenError.EXPIRED);
        }
    }
}
