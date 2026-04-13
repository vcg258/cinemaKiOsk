package com.example.cinemakiosk.repository;

import com.example.cinemakiosk.domain.adminDomain.AdminEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<AdminEntity, Long> {
    // 관리자 로그인 아이디로 조회
    Optional<AdminEntity> findByLoginId(String loginId);
}
