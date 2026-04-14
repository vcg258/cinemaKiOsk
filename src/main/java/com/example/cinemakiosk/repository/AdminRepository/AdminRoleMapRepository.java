package com.example.cinemakiosk.repository.AdminRepository;

import com.example.cinemakiosk.domain.adminDomain.AdminRoleMapEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdminRoleMapRepository extends JpaRepository<AdminRoleMapEntity, Long> {
    // 지정 관리자 권한모두 조회
    List<AdminRoleMapEntity> findByAdminEntity_AdminId(Long adminEntityAdminId);

    //관리자 아이다와 권한 아이디를 받아 권한 제거
    AdminRoleMapEntity deleteAdminRoleMapEntityByAdminEntity_AdminIdAndAdminRoleEntity_Id(Long adminId, Long roleId);
}
