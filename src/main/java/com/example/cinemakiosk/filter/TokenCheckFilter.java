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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
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
        if (!path.startsWith("/api/admin/") || path.startsWith("/api/admin/login") || path.startsWith("/api/admin/refresh")) {
            filterChain.doFilter(request, response);
            return;
        }

        log.info("doFilterInternal...");
        log.info(jwtUtil);


        // 유효한 AccessToken이 맞다면 통과 아니면 예외
        try {
            // 요청한 토큰이 검증된 애라면 Map에 담음
            Map<String, Object> claim = validateAccessToken(request);

            // Map에 담긴 로그인아이디 지정
            String loginId = (String) claim.get("loginId");
            boolean level = (boolean) claim.get("level");
            log.info("loginId: {}, level: {}", loginId, level);

            String role = level ? "ROLE_STAFF" : "ROLE_MASTER";

            // 인증된 사용자의 Id와 Pw를 담은 신분증 역할
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    loginId,
                    null,
                    List.of(new SimpleGrantedAuthority(role))
            );

            // 토큰과 auth가 있다면 인증을 모두 통과한 사용자 이므로 시큐리티에 등록
            SecurityContextHolder.getContext().setAuthentication(auth);

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
