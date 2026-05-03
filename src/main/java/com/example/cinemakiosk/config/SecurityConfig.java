package com.example.cinemakiosk.config;

import com.example.cinemakiosk.filter.APILoginFilter;
import com.example.cinemakiosk.filter.RefreshTokenFilter;
import com.example.cinemakiosk.filter.TokenCheckFilter;
import com.example.cinemakiosk.handler.APILoginSuccessHandler;
import com.example.cinemakiosk.service.adminservice.AdminDetailsService;
import com.example.cinemakiosk.service.adminservice.AdminRoleService;
import com.example.cinemakiosk.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtUtil jwtUtil;
    private final AdminDetailsService adminDetailsService;
    private final AdminRoleService adminRoleService; // RefreshToken를 DB에서 관리

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // authenticationManager를 불러옴
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);

        // 로그인 시도한 관리자 아이디를 가져옴
        authenticationManagerBuilder
                .userDetailsService(adminDetailsService)
                .passwordEncoder(passwordEncoder());

        // 검증 매니저 선언 해줌
        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();

        // 설정에 등록
        http.authenticationManager(authenticationManager);

        http
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .cors(Customizer.withDefaults()) // 시큐리티 CORS 허용
                .csrf(csrf -> csrf.disable()) // JWT (CSRF 비활성화)
                .sessionManagement(session ->
                        // STATELESS = 세션 아예 사용안함, ALWAYS = 항상 세션 생성, IF_REQUIRED = 필요할때만 생성 기본값, NEVER = 직접 안만듬 대신 있으면 사용
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션을 사용안함 (AccessToken = ViewData, RefreshToken 쿠키로 저장)
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/api/admin/**").authenticated() // 경로가 /api/admin/ 으로 시작한 API는 JWT 토큰 필요
                        // ROLE_??? 로 API를 막을 수 있지만 현재 DB(Admin_Role)로 권한을 이미 검증 하고있고 권한추가 제거 로직이 있기때문에 모두 허용
                        .anyRequest().permitAll())
                .addFilterBefore(apiLoginFilter(authenticationManager), UsernamePasswordAuthenticationFilter.class) // APILogin 필터 추가
                .addFilterBefore(tokenCheckFilter(), UsernamePasswordAuthenticationFilter.class) // AccessToken 필터 추가
                .addFilterBefore(refreshTokenFilter(), tokenCheckFilter().getClass()); // RefreshToken 필터 추가
        return http.build();
    }

    /**
     * api 로그인 검증 필터 (필터 통과 -> 성공 핸들러)
     * @param authenticationManager 시큐리티 검증 매니저
     * @return 검증된 정보
     * @throws Exception 예외
     */
    public APILoginFilter apiLoginFilter(AuthenticationManager authenticationManager) throws Exception {
        APILoginFilter filter = new APILoginFilter("/api/admin/login");
        filter.setAuthenticationManager(authenticationManager); // 매니저 등록
        filter.setAuthenticationSuccessHandler(new APILoginSuccessHandler(jwtUtil, adminRoleService)); // 성공 핸들러 등록
        return filter;
    }

    /**
     * AccessToken, RefreshToken 검증 필터
     * @return 사용자가 검증이 됐다면 Token 발급
     */
    @Bean
    public TokenCheckFilter tokenCheckFilter() {
        TokenCheckFilter filter = new TokenCheckFilter(jwtUtil);
        return filter;
    }

    /**
     * RefreshToken 재발급 필터
     * @return RefreshToken
     */
    @Bean
    public RefreshTokenFilter refreshTokenFilter() {
        return new RefreshTokenFilter("/api/admin/refresh", jwtUtil, adminRoleService);
    }

    // BCrypt (암호화)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
