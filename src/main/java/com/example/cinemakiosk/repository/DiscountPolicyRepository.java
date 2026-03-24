package com.example.cinemakiosk.repository;

import com.example.cinemakiosk.domain.DiscountPolicyEntity.DiscountPolicyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface DiscountPolicyRepository extends JpaRepository<DiscountPolicyEntity, Long> {
    // 할인 정책 이름 검증 + 기간이 남은 할인정책 확인(유무 확인) 메서드
    boolean existsByPolicyNameAndEndAtAfter(String policyName, LocalDateTime now);
}
