package com.example.cinemakiosk.repository;

import com.example.cinemakiosk.domain.AdminEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<AdminEntity, Long> {

    // 로그인 아이디로 조회
    Optional<AdminEntity> findByLoginId(String loginId);

    // uuid로 조회 (자동 로그인)
    Optional<AdminEntity> findByUuid(String uuid);

    // 로그인 아이디 중복 체크
    boolean existsByLoginId(String loginId);

    // uuid 갱신 (자동 로그인 토큰)
    @Modifying
    @Transactional
    @Query("UPDATE AdminEntity a SET a.uuid = :uuid WHERE a.adminId = :adminId")
    int updateUuid(@Param("adminId") Long adminId, @Param("uuid") String uuid);

    // uuid 삭제 (로그아웃)
    @Modifying
    @Transactional
    @Query("UPDATE AdminEntity a SET a.uuid = NULL WHERE a.adminId = :adminId")
    int clearUuid(@Param("adminId") Long adminId);
}