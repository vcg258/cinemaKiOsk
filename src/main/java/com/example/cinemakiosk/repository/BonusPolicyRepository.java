package com.example.cinemakiosk.repository;

import com.example.cinemakiosk.domain.BonusPolicyEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;

public interface BonusPolicyRepository extends JpaRepository<BonusPolicyEntity, Long>{
    // 적립 정책 이름 검증 + 기간이 남은 할인정책 확인(유무 확인) 메서드
    boolean existsByPolicyNameAndEndAtAfter(String policyName, LocalDateTime localDateTime);

}

