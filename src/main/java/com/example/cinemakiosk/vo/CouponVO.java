package com.example.cinemakiosk.vo;

import com.example.cinemakiosk.dto.CouponDTO;
import com.example.cinemakiosk.dto.PaymentDetailsDTO;
import lombok.*;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CouponVO {
    private String couponNum; // 쿠폰 번호
    private DiscountPolicyVO discountPolicy; // 할인 정책 인덱스 FK
    private boolean status; // 사용여부 (사용가능 = true, 불가능 = false)
    private PaymentDetailsVO paymentDetails;

    /**
     * VO -> DTO
     * @param couponVO VO
     * @return 변환을 위한 Builder
     */
    public static CouponDTO toDTO(CouponVO couponVO) {
        //쿠폰쪽에서 결제의 id만 받는 걸로 진행.
        PaymentDetailsDTO paymentDetailsDTO = PaymentDetailsDTO.builder()
                .id(couponVO.getPaymentDetails().getId())
                .build();

        return CouponDTO.builder()
                .couponNum(couponVO.getCouponNum())
                .discountPolicy(DiscountPolicyVO.toDTO(couponVO.getDiscountPolicy()))
                .status(couponVO.isStatus())
                .paymentDetails(paymentDetailsDTO)
                .build();
    }
}
