package com.example.cinemakiosk.repository;

import com.example.cinemakiosk.domain.CouponEntity;
import com.example.cinemakiosk.domain.enums.ConditionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CouponRepository extends JpaRepository<CouponEntity, String> {
    // 쿠폰 사용 검증 (쿠폰번호, 사용여부 True, 할인정책이 일치하는 경우 수집)
    Optional<CouponEntity> findByCouponNumAndDiscountPolicyEntityIdAndStatusTrue(String couponNum, Long policyId);

    // COUPON 타입 할인정책에 연결된 쿠폰 수 조회 (페이징 count 전용)
    long countByDiscountPolicyEntityConditionType(ConditionType conditionType);
}
