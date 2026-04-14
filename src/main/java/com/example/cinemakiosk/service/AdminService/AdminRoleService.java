package com.example.cinemakiosk.service.AdminService;

import com.example.cinemakiosk.dto.AdminDTO.AdminDTO;
import com.example.cinemakiosk.dto.AdminDTO.AdminRoleDTO;
import com.example.cinemakiosk.dto.AdminDTO.AdminRoleMapDTO;
import com.example.cinemakiosk.dto.RequestDTO.AdminRoleMapRequest;

import java.util.List;

public interface AdminRoleService {
    // 전체 직원 조회 (직원의 해당 권한까지 조회)
    List<AdminDTO> getAdmins();

    // 권한 전체 조회
    List<AdminRoleDTO> getRoles();

    // 해당 직원 권한 조회
    List<AdminRoleMapDTO> getAdminRoleMaps(Long adminId);

    // 해당 직원 권한 부여 (초기화 기능이 있기때문에 따로 삭제 기능 구현안함)
    void addRole(AdminRoleMapRequest adminRoleMapRequest);

}