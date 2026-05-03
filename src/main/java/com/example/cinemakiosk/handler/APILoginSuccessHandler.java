package com.example.cinemakiosk.handler;

import com.example.cinemakiosk.service.adminservice.AdminDetails;
import com.example.cinemakiosk.service.adminservice.AdminRoleService;
import com.example.cinemakiosk.util.JwtUtil;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
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
    private final AdminRoleService adminRoleService; // RefreshToken DB저장을 위함

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("Login Success Handler...");

        log.info(authentication);
        log.info("성공 핸들러 사용자 이름 :  {}", authentication.getName());

        // 성공한 유저의 데이터를 AdminUserDetails 에서 가져옴 (아이디, 비밀번호, Level)을 가져오기 위함
        AdminDetails adminDetails = (AdminDetails) authentication.getPrincipal();
        boolean level = adminDetails.isLevel();

        // 자동 로그인 여부 확인을 위한 값
        String autoLogin = (String) request.getAttribute("autoLogin");
        boolean isAutoLogin = "true".equals(autoLogin);

        int refreshTokenPeriod = isAutoLogin ? 60 * 24 * 30 : 60 * 24; // 자동로그인이면 1달 : 일반 로그인 하루 (얘는 분단위)
        int cookieMaxAge = isAutoLogin ? 60 * 60 * 24 * 30 : 60 * 60 * 24; // 동일 (쿠키는 초단위임)


        // JWT 토큰에 담을 아이디와 권한 레벨
        Map<String, Object> claim = Map.of(
                "loginId", authentication.getName(),
                "level", level,
                "autoLogin", autoLogin
        );

        log.info("autoLogin 값: {}, isAutoLogin: {}", autoLogin, isAutoLogin);
        log.info("refreshTokenPeriod: {}, cookieMaxAge: {}", refreshTokenPeriod, cookieMaxAge);

        // AccessToken 유효기간 설정 (30분)
        String accessToken = jwtUtil.generateToken(claim, 30);
        // RefreshToken 유효기간 설정
        String refreshToken = jwtUtil.generateToken(claim, refreshTokenPeriod);
        log.info("AccessToken: {}", accessToken);
        log.info("RefreshToken: {}", refreshToken);

        adminRoleService.rememberMe(authentication.getName(), refreshToken); // DB에 리프레시 저장

        // refreshToken HttpOnlyCookie로 저장 (XSS 방지)
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true) // HttpOnly
                .secure(false) // HTTPS 환경일 경우 true (https 연결에서만 전송할 것인지 지정 하는것임 현재는 http 이므로 false)
                .path("/")
                .maxAge(cookieMaxAge) // 한달
                .sameSite("Strict") // 이 사이트에서 발생한 쿠키만 허용함 (이건 CSRF 방어용인데 RefreshToken이 HttpOnlyCookie를 사용하기 떄문에 걸어둠)
                .build();

        // 헤더로 보내야지 JS 없이 브라우저가 자동 전송을 해주고 HttpOnlyCookie 사용 가능함
        response.setHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        // 권한 꺼내기 (여러권한이 있기 때문에 List로 받음)
        List<GrantedAuthority> authorities = (List<GrantedAuthority>) authentication.getAuthorities();
        List<String> role = new ArrayList<>();
        for (GrantedAuthority grantedAuthority : authorities) {
            role.add(grantedAuthority.getAuthority());
        }

        // Map에 AccessToken과 level, role를 묶음 (성공한 사용자의 권한과 토큰을 클라이언트에 주기위함)
        Map<String, Object> keyMap = Map.of(
                "accessToken", accessToken,
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
