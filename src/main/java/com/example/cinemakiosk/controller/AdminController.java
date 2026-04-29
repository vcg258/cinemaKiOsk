package com.example.cinemakiosk.controller;



import com.example.cinemakiosk.dto.adminDTO.AdminDTO;
import com.example.cinemakiosk.dto.adminDTO.AdminRoleDTO;
import com.example.cinemakiosk.dto.adminDTO.AdminRoleMapDTO;
import com.example.cinemakiosk.dto.requestDTO.AdminRoleMapRequest;
import com.example.cinemakiosk.service.adminservice.AdminRoleService;
import com.example.cinemakiosk.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Log4j2
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final JwtUtil jwtUtil;
    private final AdminRoleService adminRoleService;

    @Operation(summary = "전체 직원 및 해당 권한 조회")
    @GetMapping("/list")
    public ResponseEntity<List<AdminDTO>> getAllAdmin() {
        return ResponseEntity.ok(adminRoleService.getAdmins());
    }

    @Operation(summary = "전체 권한 조회")
    @GetMapping("/role/list")
    public ResponseEntity<List<AdminRoleDTO>> getAllAdminRole() {
        return ResponseEntity.ok(adminRoleService.getRoles());
    }

    @Operation(summary = "지정 관리자 권한 조회")
    @GetMapping("/role/{loginId}")
    public ResponseEntity<List<AdminRoleMapDTO>> getAdminRole(@PathVariable Long loginId) {
        return ResponseEntity.ok(adminRoleService.getAdminRoleMaps(loginId));
    }

    @Operation(summary = "지정 관리자 권한 부여 및 제거")
    @PostMapping("/role")
    public ResponseEntity<Void> addRole(@RequestBody AdminRoleMapRequest adminRoleMapRequest) {
        adminRoleService.addRole(adminRoleMapRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @Operation(summary = "자동 로그인")
    @PostMapping("/remember_me")
    public ResponseEntity<Void> rememberMe(String loginId, HttpServletResponse response) {
        log.info("자동로그인 메서드 진입 쿠키 생성");
        // UUID 추가
        adminRoleService.rememberMe(loginId);

        Cookie cookie = new Cookie("remember-me", adminRoleService.getAdmin(loginId).getUuid());
        cookie.setHttpOnly(true); // JS 접근 불가 설정
        cookie.setSecure(true); // HTTP 환경에서만 전송
        cookie.setMaxAge(60 * 60 * 24 * 7); // TODO 일주일 지정
        cookie.setPath("/"); // 쿠키 적용 경로
        response.addCookie(cookie);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "자동 로그인 검증")
    @PostMapping("/remember_me/auth")
    public ResponseEntity<String> rememberMeAuth(HttpServletRequest request) {
        log.info("자동로그인 검증 진입");
        // 쿠키가 없을 경우
        if (request.getCookies() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 401
        }

        for (Cookie cookie : request.getCookies()) {
            if ("remember-me".equals(cookie.getName())) {
                String uuid = cookie.getValue();

                AdminDTO admin = adminRoleService.getAdminByRememberMe(uuid);
                return ResponseEntity.ok(jwtUtil.generateToken(Map.of("loginId", admin.getLoginId()), 1));
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 로그인시도한 아이디와 동일한 쿠키 없음 401
    }

    @Operation(summary = "로그아웃")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(String loginId, HttpServletResponse response) {

        // UUID null처리
        adminRoleService.logout(loginId);

        // 쿠키 제거
        Cookie cookie = new Cookie("remember-me", null);
        cookie.setHttpOnly(true); // JS 접근 불가 설정
        cookie.setSecure(true); // HTTP 환경에서만 전송
        cookie.setMaxAge(0);
        cookie.setPath("/"); // 쿠키 적용 경로
        response.addCookie(cookie);

        return ResponseEntity.ok().build();
    }
}
