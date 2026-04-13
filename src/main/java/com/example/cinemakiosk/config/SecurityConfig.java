package com.example.cinemakiosk.config;

import com.example.cinemakiosk.filter.APILoginFilter;
import com.example.cinemakiosk.filter.RefreshTokenFilter;
import com.example.cinemakiosk.filter.TokenCheckFilter;
import com.example.cinemakiosk.handler.APILoginSuccessHandler;
import com.example.cinemakiosk.service.AdminService.AdminDetailsService;
import com.example.cinemakiosk.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
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

    // TODO AuthenticationManagerBuilder, AdminDetailsService + Entity 추가해야함 (미완)
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
                .csrf(csrf -> csrf.disable()) // JWT를 이용하기 때문에 CSRF 비활성화
                .sessionManagement(session ->
                        // STATELESS = 세션 아예 사용안함, ALWAYS = 항상 세션 생성, IF_REQUIRED = 필요할때만 생성 기본값, NEVER = 직접 안만듬 대신 있으면 사용
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션을 사용안함 (JWT 로컬스토리지에 저장, 쿠키로 저장 할 수도 있긴함)
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/api/admin/**").authenticated() // 경로가 /api/admin/ 으로 시작한 API는 JWT 토큰 필요
                        .anyRequest().permitAll()) // 일단 전체 허용
                .addFilterBefore(apiLoginFilter(authenticationManager), UsernamePasswordAuthenticationFilter.class) // APILogin 필터 추가
                .addFilterBefore(tokenCheckFilter(), UsernamePasswordAuthenticationFilter.class) // AccessToken 필터 추가
                .addFilterBefore(refreshTokenFilter(), tokenCheckFilter().getClass()); // RefreshToken 필터 추가
        return http.build();
    }


    public APILoginFilter apiLoginFilter(AuthenticationManager authenticationManager) throws Exception {
        APILoginFilter filter = new APILoginFilter("/api/admin/login");
        filter.setAuthenticationManager(authenticationManager); // 매니저 등록
        filter.setAuthenticationSuccessHandler(new APILoginSuccessHandler(jwtUtil)); // 성공 핸들러 등록
        return filter;
    }

    @Bean
    public TokenCheckFilter tokenCheckFilter() {
        TokenCheckFilter filter = new TokenCheckFilter(jwtUtil);
        return filter;
    }

    @Bean
    public RefreshTokenFilter refreshTokenFilter() {
        return new RefreshTokenFilter("/api/admin/refresh", jwtUtil);
    }

    // BCrypt (암호화)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
