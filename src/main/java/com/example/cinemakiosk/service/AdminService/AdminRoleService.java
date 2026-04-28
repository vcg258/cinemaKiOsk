package com.example.cinemakiosk.service.AdminService;

import com.example.cinemakiosk.dto.adminDTO.AdminDTO;
import com.example.cinemakiosk.dto.adminDTO.AdminRoleDTO;
import com.example.cinemakiosk.dto.adminDTO.AdminRoleMapDTO;
import com.example.cinemakiosk.dto.requestDTO.AdminRoleMapRequest;

import java.util.List;

public interface AdminRoleService {
    // 전체 직원 조회 (직원의 해당 권한까지 조회)
    List<AdminDTO> getAdmins();

    // 지정 관리자 조회
    AdminDTO getAdmin(String loginId);

    // 자동로그인을 위한 UUID로 조회
    AdminDTO getAdminByRememberMe(String uuid);

    // 권한 전체 조회
    List<AdminRoleDTO> getRoles();

    // 해당 직원 권한 조회
    List<AdminRoleMapDTO> getAdminRoleMaps(Long adminId);

    // 해당 직원 권한 부여 (초기화 기능이 있기때문에 따로 삭제 기능 구현안함)
    void addRole(AdminRoleMapRequest adminRoleMapRequest);

    // 자동 로그인 UUID Update
    void rememberMe(String loginId);

    // 로그아웃
    void logout(String loginId);
}