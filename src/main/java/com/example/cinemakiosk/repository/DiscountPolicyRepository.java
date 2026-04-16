package com.example.cinemakiosk.repository;

import com.example.cinemakiosk.domain.DiscountPolicyEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DiscountPolicyRepository extends JpaRepository<DiscountPolicyEntity, Long> {
    // 할인 정책 이름 검증 + 기간이 남은 할인정책 확인 (유무 확인) 메서드
    boolean existsByPolicyNameAndEndAtAfter(String policyName, LocalDateTime now);

    // 할인정책 오늘포함 이후 날짜 모두 조회
    List<DiscountPolicyEntity> findAllByEndAtAfter(LocalDateTime endAtAfter);
}
