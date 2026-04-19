package com.example.cinemakiosk.repository;

import com.example.cinemakiosk.domain.BonusPolicyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BonusPolicyRepository extends JpaRepository<BonusPolicyEntity, Long>{
    // 적립 정책 이름 검증 + 기간이 남은 할인정책 확인(유무 확인) 메서드
    boolean existsByPolicyNameAndEndAtAfter(String policyName, LocalDateTime localDateTime);

    // 적립 정책 오늘 포함 이후 날짜 모두 조회 만료일이 NULL 이면 무기한이므로 포함
    @Query("SELECT B FROM BonusPolicyEntity AS B WHERE B.endAt IS NULL OR B.endAt >= :NOW")
    List<BonusPolicyEntity> findAllBonusPolicy(@Param("NOW") LocalDateTime now);
}

