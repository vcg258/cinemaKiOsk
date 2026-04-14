package com.example.cinemakiosk.service.AdminService;

import com.example.cinemakiosk.domain.adminDomain.AdminRoleEntity;
import com.example.cinemakiosk.domain.adminDomain.AdminRoleMapEntity;
import com.example.cinemakiosk.dto.AdminDTO.AdminDTO;
import com.example.cinemakiosk.dto.AdminDTO.AdminRoleDTO;
import com.example.cinemakiosk.dto.AdminDTO.AdminRoleMapDTO;

import java.util.List;

public interface AdminRoleService {
    // 전체 직원 조회
    List<AdminDTO> getAdmins();

    // 권한 전체 조회
    List<AdminRoleDTO> getRoles();

    // 해당 직원 권한 조회
    List<AdminRoleMapDTO> getAdminRoleMaps(Long adminId);

    // 해당 직원 권한 부여
    void addRole(AdminRoleMapDTO adminRoleMapDTO);

    // 해당 직원 권한 삭제
    void deleteRole(AdminRoleMapDTO adminRoleMapDTO);
}