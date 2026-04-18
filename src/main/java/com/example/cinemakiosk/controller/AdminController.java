package com.example.cinemakiosk.controller;

import com.example.cinemakiosk.dto.AdminDTO.AdminDTO;
import com.example.cinemakiosk.dto.AdminDTO.AdminRoleDTO;
import com.example.cinemakiosk.dto.AdminDTO.AdminRoleMapDTO;
import com.example.cinemakiosk.dto.RequestDTO.AdminRoleMapRequest;
import com.example.cinemakiosk.service.AdminService.AdminRoleService;
import com.example.cinemakiosk.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.fasterxml.jackson.databind.type.LogicalType.Map;

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


//    @Operation(summary = "자동 로그인")
//    @PostMapping("/remember_me")
//    public ResponseEntity<Void> rememberMe(String loginId, HttpServletResponse response) {
//        // UUID 추가
//        adminRoleService.rememberMe(loginId);
//
//        Cookie cookie = new Cookie("remember-me", adminRoleService.getAdmin(loginId).getUuid());
//        cookie.setHttpOnly(true);
//        cookie.setSecure(true);
//        cookie.setMaxAge(60 * 60 * 24 * 7); // TODO 일주일 지정
//        cookie.setPath("/");
//        response.addCookie(cookie);
//        return ResponseEntity.ok().build();
//    }
//
//    @Operation(summary = "자동 로그인 검증")
//    @PostMapping("/remember_me/auth")
//    public ResponseEntity<Void> rememberMeAuth(HttpServletRequest request) {
//
//        // 쿠키가 없을 경우
//        if (request.getCookies() == null) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 401
//        }
//
//        for (Cookie cookie : request.getCookies()) {
//            if ("remember-me".equals(cookie.getName())) {
//                String uuid = cookie.getValue();
//
//                adminRoleService.getAdminByRememberMe(uuid);
////                return ResponseEntity.ok(jwtUtil.generateToken(java.util.Map.of("loginId", )));
//            }
//        }
//        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 로그인시도한 아이디와 동일한 쿠키 없음 401
//    }
}
