package com.example.cinemakiosk.controller;

import com.example.cinemakiosk.dto.AdminDTO.AdminDTO;
import com.example.cinemakiosk.dto.AdminDTO.AdminRoleDTO;
import com.example.cinemakiosk.dto.AdminDTO.AdminRoleMapDTO;
import com.example.cinemakiosk.dto.RequestDTO.AdminRoleMapRequest;
import com.example.cinemakiosk.service.AdminService.AdminRoleService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

}
