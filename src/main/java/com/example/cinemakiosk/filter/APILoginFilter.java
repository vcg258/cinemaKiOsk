package com.example.cinemakiosk.filter;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;

@Log4j2
public class APILoginFilter extends AbstractAuthenticationProcessingFilter {
    public APILoginFilter(String defaultFilterProcessesUrl) {
        super(defaultFilterProcessesUrl);
    }

    // API 로그인을 위한 필터
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        log.info("Login attemptAuthentication()...");

        // GET방식 접근 X
        if (request.getMethod().equalsIgnoreCase("GET")) {
            log.info("GET 방식 접근 불가");
            return null;
        }

        Map<String, String> jsonDate = parseRequestJSON(request);
        log.info("API JSON 로그인 데이터 = {}", jsonDate);

        // 미인증 토큰 생성
        UsernamePasswordAuthenticationToken authenticationToken
                = new UsernamePasswordAuthenticationToken(
                jsonDate.get("loginId"),
                jsonDate.get("password"));
        // 인증 매니저 한테 검증을 의뢰
        return getAuthenticationManager().authenticate(authenticationToken);
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
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }
}
