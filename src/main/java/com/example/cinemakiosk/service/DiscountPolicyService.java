package com.example.cinemakiosk.service;

import org.springframework.stereotype.Service;

public interface DiscountPolicyService {
    // 특정 정책의 쿠폰 번호 발행
    void createCouponNum(Long policyId);

    // 쿠폰 사용 검증 (정책 기간, 사용여부, 정책에 해당하는 쿠폰)
    boolean authCoupon(Long policyId);

    // 쿠폰을 사용함으로써 사용여부 업데이트
    void updateStatus(Long policyId);

    // 할인정책 종료 (23시 59분으로 지정 활성화 여부 FALSE)
    void finishActivation(Long id);
}