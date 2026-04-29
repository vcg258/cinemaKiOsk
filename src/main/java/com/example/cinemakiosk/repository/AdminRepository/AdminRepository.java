package com.example.cinemakiosk.repository.AdminRepository;

import com.example.cinemakiosk.domain.admindomain.AdminEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<AdminEntity, Long> {
    // 관리자 로그인아이디로 조회
    Optional<AdminEntity> findByLoginId(String loginId);

    // 관리자 UUID로 조회
    AdminEntity findByUuid(String uuid);
}
