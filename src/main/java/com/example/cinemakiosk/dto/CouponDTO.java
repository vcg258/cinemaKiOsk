package com.example.cinemakiosk.dto;

import com.example.cinemakiosk.domain.CouponEntity;
import com.example.cinemakiosk.domain.PaymentDetailsEntity.PaymentDetailsEntity;
import com.example.cinemakiosk.vo.CouponVO;
import com.example.cinemakiosk.vo.PaymentDetailsVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponDTO {
    private String couponNum; // 쿠폰 번호
    private DiscountPolicyDTO discountPolicy; // 할인 정책 인덱스 FK
    private boolean status; // 사용여부 (사용가능 = true, 불가능 = false)
    private PaymentDetailsDTO paymentDetails; // 1:1

    /**
     * DTO -> Entity
     * @param couponDTO DTO
     * @return 변환을 위한 Builder
     */
    public static CouponEntity toEntity(CouponDTO couponDTO) {
        //쿠폰쪽에서 결제의 id만 받는 걸로 진행.
        PaymentDetailsEntity paymentDetailsEntity = PaymentDetailsEntity.builder()
                .id(couponDTO.getPaymentDetails().getId())
                .build();

        return CouponEntity.builder()
                .couponNum(couponDTO.getCouponNum())
                .discountPolicyEntity(DiscountPolicyDTO.toEntity(couponDTO.getDiscountPolicy()))
                .status(couponDTO.isStatus())
                .paymentDetailsEntity(paymentDetailsEntity)
                .build();
    }

    /**
     * DTO -> VO
     * @param couponDTO DTO
     * @return 변환을 위한 Builder
     */
    public static CouponVO toVO(CouponDTO couponDTO) {
        //쿠폰쪽에서 결제의 id만 받는 걸로 진행.
        PaymentDetailsVO paymentDetailsVO = PaymentDetailsVO.builder()
                .id(couponDTO.getPaymentDetails().getId())
                .build();

        return CouponVO.builder()
                .couponNum(couponDTO.getCouponNum())
                .discountPolicy(DiscountPolicyDTO.toVO(couponDTO.getDiscountPolicy()))
                .status(couponDTO.isStatus())
                .paymentDetails(paymentDetailsVO)
                .build();
    }
}
