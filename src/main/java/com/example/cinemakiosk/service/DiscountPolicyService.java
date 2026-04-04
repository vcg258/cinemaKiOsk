package com.example.cinemakiosk.service;

import com.example.cinemakiosk.dto.CouponDTO;
import com.example.cinemakiosk.dto.DiscountPolicyDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface DiscountPolicyService {
    // 할인 정책 추가 / 수정
    void createDiscountPolicy(DiscountPolicyDTO discountPolicyDTO);

    // 할인 전체 조회
    List<DiscountPolicyDTO> getDiscountPolicies();

    // 할인 단일 조회 (?)
    DiscountPolicyDTO getDiscountPolicy(Long id);

    // 할인정책 종료 (23시 59분으로 지정 활성화 여부 FALSE)
    void finishActivation(Long id);

    // 할인정책 활성화 / 비활성화
    void changeActivation(List<Long> ids, boolean activation);

    // 특정 정책의 쿠폰 번호 발행
    void createCouponNum(Long policyId);

    // 쿠폰 사용 검증 (정책 기간, 사용여부, 정책에 해당하는 쿠폰)
    boolean authCoupon(CouponDTO couponDTO);

    // 쿠폰을 사용함으로써 사용여부 업데이트 및 환불로 인한 복구
    void updateStatus(CouponDTO couponDTO);

    // 페이징 처리 (로그까지 전체 조회)
    Page<DiscountPolicyDTO> getDiscountPolicyPage(int page);

    // 쿠폰 전체 조회
    List<CouponDTO> getCouponAll();

    // 쿠폰 단일 조회
    CouponDTO getCoupon(String couponNum);

}