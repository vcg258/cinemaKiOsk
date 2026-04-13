package com.example.cinemakiosk.service.AdminService;

import com.example.cinemakiosk.domain.adminDomain.AdminRoleEntity;
import com.example.cinemakiosk.domain.adminDomain.AdminRoleMapEntity;
import com.example.cinemakiosk.dto.AdminDTO.AdminRoleDTO;
import com.example.cinemakiosk.dto.AdminDTO.AdminRoleMapDTO;
import com.example.cinemakiosk.repository.AdminRepository.AdminRepository;
import com.example.cinemakiosk.repository.AdminRepository.AdminRoleMapRepository;
import com.example.cinemakiosk.repository.AdminRepository.AdminRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class AdminRoleServiceImpl implements AdminRoleService {
    private final AdminRepository adminRepository;
    private final AdminRoleRepository adminRoleRepository;
    private final AdminRoleMapRepository adminRoleMapRepository;

    /**
     * 권한 전체 조회
     * @return 전체 권한 리스트
     */
    @Override
    public List<AdminRoleDTO> getRoles() {
        List<AdminRoleEntity> roleEntities = adminRoleRepository.findAll();
        return roleEntities.stream().map(AdminRoleEntity::toDTO).toList();
    }

    /**
     * 지정 회원의 권한 조회
     * @param adminId 관리자 FK
     * @return 지정 회원 권한 조회
     */
    @Override
    public List<AdminRoleMapDTO> getAdminRoleMaps(Long adminId) {
        List<AdminRoleMapEntity> adminRoleMapEntities = adminRoleMapRepository.findByAdminEntity_AdminId(adminId);
        return adminRoleMapEntities.stream().map(AdminRoleMapEntity::toDTO).toList();
    }

    /**
     * 직원에게 권한 부여
     * @param adminRoleMapDTO 권한 매핑 DTO
     */
    @Override
    public void addRole(AdminRoleMapDTO adminRoleMapDTO) {

    }

    /**
     * @param adminRoleMapDTO
     */
    @Override
    public void deleteRole(AdminRoleMapDTO adminRoleMapDTO) {

    }
}
