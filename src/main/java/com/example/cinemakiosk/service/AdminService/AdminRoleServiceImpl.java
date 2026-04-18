package com.example.cinemakiosk.service.AdminService;

import com.example.cinemakiosk.domain.adminDomain.AdminEntity;
import com.example.cinemakiosk.domain.adminDomain.AdminRoleEntity;
import com.example.cinemakiosk.domain.adminDomain.AdminRoleMapEntity;
import com.example.cinemakiosk.dto.AdminDTO.AdminDTO;
import com.example.cinemakiosk.dto.AdminDTO.AdminRoleDTO;
import com.example.cinemakiosk.dto.AdminDTO.AdminRoleMapDTO;
import com.example.cinemakiosk.dto.RequestDTO.AdminRoleMapRequest;
import com.example.cinemakiosk.mapper.AdminMapper;
import com.example.cinemakiosk.repository.AdminRepository.AdminRepository;
import com.example.cinemakiosk.repository.AdminRepository.AdminRoleMapRepository;
import com.example.cinemakiosk.repository.AdminRepository.AdminRoleRepository;
import com.example.cinemakiosk.vo.AdminVO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Log4j2
@Service
@RequiredArgsConstructor
public class AdminRoleServiceImpl implements AdminRoleService {
    private final AdminRepository adminRepository;
    private final AdminMapper adminMapper;
    private final AdminRoleRepository adminRoleRepository;
    private final AdminRoleMapRepository adminRoleMapRepository;

    /**
     * 전체 직원 조회 (직원의 해당 권한까지 조회)
     * @return 전체 직원 리스트
     */
    @Override
    public List<AdminDTO> getAdmins() {
        List<AdminVO> vo = adminMapper.selectAdminByAdminRole();
        return vo.stream().map(AdminVO::toDTO).toList();
    }

    /**
     * 지정 관리자 조회
     * @param loginId 관리자 아이다
     * @return 지정 관리자
     */
    @Override
    public AdminDTO getAdmin(String loginId) {
        AdminEntity admin = adminRepository.findByLoginId(loginId).orElseThrow();
        return AdminEntity.toDTO(admin);
    }

    /**
     * UUID에 해당하는 관리자 조회
     * @param uuid UUID
     * @return 해당 UUID에 해당하는 관리자
     */
    @Override
    public AdminDTO getAdminByRememberMe(String uuid) {
        AdminEntity entity = adminRepository.findByUuid(uuid);
        return AdminEntity.toDTO(entity);
    }


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
     * 지정 관리자의 권한 조회
     * @param adminId 관리자 FK
     * @return 지정 회원 권한 조회
     */
    @Override
    public List<AdminRoleMapDTO> getAdminRoleMaps(Long adminId) {
        List<AdminRoleMapEntity> adminRoleMapEntities = adminRoleMapRepository.findByAdminEntity_AdminId(adminId);
        return adminRoleMapEntities.stream().map(AdminRoleMapEntity::toDTO).toList();
    }

    /**
     * 직원에게 권한 부여 (초기화 기능이 있기때문에 따로 삭제 기능 구현안함)
     * @param adminRoleMapRequest 권한 매핑 DTO
     */
    @Override
    @Transactional
    public void addRole(AdminRoleMapRequest adminRoleMapRequest) {
        // 일단 해당 직원의 정책 모두 초기화
        adminRoleMapRepository.deleteAllByAdminId(adminRoleMapRequest.getAdminId());

        AdminEntity staff = adminRepository.findById(adminRoleMapRequest.getAdminId()).orElseThrow(
                () -> new RuntimeException("직원을 찾을 수 없습니다.")
        );

        for (Long roleId : adminRoleMapRequest.getRoles()) {
            if (!adminRoleRepository.existsById(roleId)) {
                log.error("추가할 권한은 존재하지 않는 권한임 PASS");
                return;
            }
            AdminRoleMapEntity adminRoleMapEntity = AdminRoleMapEntity.builder()
                    .adminEntity(staff)
                    .adminRoleEntity(adminRoleRepository.getReferenceById(roleId))
                    .build();
            adminRoleMapRepository.save(adminRoleMapEntity);
            log.info("권한 부여 : {}, {}", adminRoleMapEntity.getAdminEntity().getLoginId(),
                    adminRoleMapEntity.getAdminRoleEntity().getRoleName());
        }
    }

    /**
     * 자동로그인 UUID
     * @param loginId 자동로그인 한 아이디
     */
    @Override
    public void rememberMe(String loginId) {
        AdminEntity adminEntity = adminRepository.findByLoginId(loginId).orElseThrow();
        adminEntity.changeUUID();
        log.info(adminEntity);
        adminRepository.save(adminEntity);
    }

    /**
     * 로그아웃 UUID Null 처리
     * @param loginId 로그아웃 할 사용자 아이디
     */
    @Override
    public void logout(String loginId) {
        AdminEntity adminEntity = adminRepository.findByLoginId(loginId).orElseThrow();
        adminEntity.changeUUIDNull();
        adminRepository.save(adminEntity);
    }
}
