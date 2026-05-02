package com.example.cinemakiosk.controller;


import com.example.cinemakiosk.dto.adminDTO.AdminDTO;
import com.example.cinemakiosk.dto.adminDTO.AdminRoleDTO;
import com.example.cinemakiosk.dto.adminDTO.AdminRoleMapDTO;
import com.example.cinemakiosk.dto.requestDTO.AdminRoleMapRequest;
import com.example.cinemakiosk.service.adminservice.AdminRoleService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Log4j2
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
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

    @Operation(summary = "로그아웃")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestParam String loginId, HttpServletResponse response) {

        // UUID null처리
        adminRoleService.logout(loginId);

        // 쿠키 제거 (httpOnlyCookie)
        ResponseCookie cookie = ResponseCookie.from("refreshToken", null)
                .httpOnly(true) // XSS 방지
                .secure(false) // http 사용
                .path("/") // 전체 경로 쿠키 넘겨줌
                .maxAge(0) // 기한 0 삭제
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok().build();
    }
}
