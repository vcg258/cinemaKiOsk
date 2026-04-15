package com.example.cinemakiosk.handler;

import com.example.cinemakiosk.service.AdminService.AdminDetails;
import com.example.cinemakiosk.service.AdminService.AdminDetailsService;
import com.example.cinemakiosk.util.JwtUtil;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Log4j2
@RequiredArgsConstructor
public class APILoginSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("Login Success Handler...");

        log.info(authentication);
        log.info("성공 핸들러 사용자 이름 :  {}", authentication.getName());

        // 성공한 AdminUserDetails 에서 가져옴 (아이디, 비밀번호, 권한Level)을 가져오기 위함
        AdminDetails adminDetails = (AdminDetails) authentication.getPrincipal();
        boolean level = adminDetails.isLevel();

        // JWT 토큰에 담을 아이디
        Map<String, Object> claim = Map.of(
                "loginId", authentication.getName(),
                "level", level
        );

        // TODO AccessToken 유효기간 설정
        String accessToken = jwtUtil.generateToken(claim, 1);
        // TODO RefreshToken 유효기간 설정
        String refreshToken = jwtUtil.generateToken(claim, 30);
        log.info("AccessToken: {}", accessToken);
        log.info("RefreshToken: {}", refreshToken);

        // 권한 꺼내기
        List<GrantedAuthority> authorities = (List<GrantedAuthority>) authentication.getAuthorities();
        List<String> role = new ArrayList<>();
        for (GrantedAuthority grantedAuthority : authorities) {
            role.add(grantedAuthority.getAuthority());
        }

        // 성공했을 경우 Map에 AccessToken과 RefreshToken 묶음
        Map<String, Object> keyMap = Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken,
                "level", level,
                "role", role
        );
        log.info("Map : {}", keyMap);

        // 토큰이 담긴 Map을 JSON으로 변환
        Gson gson = new Gson();
        String jsonStr = gson.toJson(keyMap);

        // 타입 지정 (JSON)
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        // 클라이언트에 전송
        response.getWriter().println(jsonStr);
    }
}
