package com.example.cinemakiosk.service.adminservice;

import com.example.cinemakiosk.domain.admindomain.AdminEntity;
import com.example.cinemakiosk.domain.admindomain.AdminRoleEntity;
import com.example.cinemakiosk.domain.admindomain.AdminRoleMapEntity;
import com.example.cinemakiosk.dto.adminDTO.AdminDTO;
import com.example.cinemakiosk.dto.adminDTO.AdminRoleDTO;
import com.example.cinemakiosk.dto.adminDTO.AdminRoleMapDTO;
import com.example.cinemakiosk.dto.requestDTO.AdminRoleMapRequest;
import com.example.cinemakiosk.mapper.AdminMapper;
import com.example.cinemakiosk.repository.admin.AdminRepository;
import com.example.cinemakiosk.repository.admin.AdminRoleMapRepository;
import com.example.cinemakiosk.repository.admin.AdminRoleRepository;
import com.example.cinemakiosk.vo.AdminVO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

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
        // 관리자 권한 검증
        String loginId = SecurityContextHolder.getContext().getAuthentication().getName();
        validatePermission(loginId, "ROLE_ADMIN_MANAGEMENT");

        List<AdminVO> vo = adminMapper.selectAdminByAdminRoles();
        return vo.stream().map(AdminVO::toDTO).toList();
    }

    /**
     * 지정 관리자 권한 검증
     * @param loginId 관리자 로그인 아이디
     * @param permission 검증 권한
     */
    @Override
    public void validatePermission(String loginId, String permission) {
        AdminVO vo = adminMapper.selectAdminByAdminRole(loginId);
        if (vo == null) {
            throw new NoSuchElementException("해당 관리자는 존재하지 않습니다.");
        }
        if (!vo.isLevel()) {
            log.info("마스터 관리자 권한 검증 불필요 패스");
            return;
        }
        if (!vo.getPermissions().contains(permission)) {
            throw new AccessDeniedException(permission + " 권한이 없습니다. 요청 기각");
        }
        log.info("{} 권한 확인 완료 검증 통과", permission);
    }

    /**
     * 권한 전체 조회
     * @return 전체 권한 리스트
     */
    @Override
    public List<AdminRoleDTO> getRoles() {
        // 관리자 권한 검증
        String loginId = SecurityContextHolder.getContext().getAuthentication().getName();
        validatePermission(loginId, "ROLE_ADMIN_MANAGEMENT");

        List<AdminRoleEntity> roleEntities = adminRoleRepository.findAll();
        return roleEntities.stream().map(AdminRoleEntity::toDTO).toList();
    }

    /**
     * 지정 관리자의 권한 조회
     * @param loginId 관리자 로그인 아이디
     * @return 지정 회원 권한 조회
     */
    @Override
    public List<AdminRoleMapDTO> getAdminRoleMaps(String loginId) {
        // 관리자 권한 검증 (요청자 본인의 권한을 검증해야 하며, 조회 대상(loginId)의 권한이 아님)
        String requesterId = SecurityContextHolder.getContext().getAuthentication().getName();
        validatePermission(requesterId, "ROLE_ADMIN_MANAGEMENT");

        List<AdminRoleMapEntity> adminRoleMapEntities = adminRoleMapRepository.findByAdminEntity_LoginId(loginId);
        return adminRoleMapEntities.stream().map(AdminRoleMapEntity::toDTO).toList();
    }

    /**
     * 직원에게 권한 부여 (초기화 기능이 있기때문에 따로 삭제 기능 구현안함)
     * @param adminRoleMapRequest 권한 매핑 DTO
     */
    @Override
    @Transactional
    public void addRole(AdminRoleMapRequest adminRoleMapRequest) {
        // 관리자 권한 검증
        String loginId = SecurityContextHolder.getContext().getAuthentication().getName();
        validatePermission(loginId, "ROLE_ADMIN_MANAGEMENT");

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
     * DB refreshToken View, Data refreshToken과 비교하기 위한 메서드
     * @param loginId 해당 관리자 로그인 아이디
     * @return DB refreshToken
     */
    @Override
    public String getRefreshToken(String loginId) {
        AdminEntity entity = adminRepository.findByLoginId(loginId).orElseThrow();
        log.info("DB refreshToken 조회 : {}", entity.getRefreshToken());
        return entity.getRefreshToken();
    }

    /**
     * 자동로그인을 위한 refreshToken DB 저장
     * @param loginId 자동로그인 요청한 사용자 아이디
     * @param refreshToken 저장할 refreshToken
     */
    @Override
    public void rememberMe(String loginId, String refreshToken) {
        AdminEntity adminEntity = adminRepository.findByLoginId(loginId).orElseThrow();
        adminEntity.changeRefreshToken(refreshToken);
        log.info("해당 사용자 계정 refreshToken 저장 : {}", adminEntity);
        adminRepository.save(adminEntity);
    }

    /**
     * 로그아웃 refreshToken Null 처리
     * @param loginId 로그아웃 할 사용자 아이디
     */
    @Override
    public void logout(String loginId) {
        AdminEntity adminEntity = adminRepository.findByLoginId(loginId).orElseThrow();
        adminEntity.changeRefreshTokenNull();
        adminRepository.save(adminEntity);
        log.info("로그아웃 시도한 계정 : {}", adminEntity);
    }
}
