package com.example.cinemakiosk.repository.AdminRepository;

import com.example.cinemakiosk.domain.adminDomain.AdminRoleMapEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AdminRoleMapRepository extends JpaRepository<AdminRoleMapEntity, Long> {
    // 지정 관리자 권한모두 조회
    List<AdminRoleMapEntity> findByAdminEntity_AdminId(Long adminEntityAdminId);

    // 지정 관리자 권한 초기화
    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM AdminRoleMapEntity a WHERE a.adminEntity.adminId = :adminId")
    void deleteAllByAdminId(Long adminId);
}
