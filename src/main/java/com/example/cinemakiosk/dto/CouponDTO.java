package com.example.cinemakiosk.dto;

import com.example.cinemakiosk.domain.CouponEntity;
import com.example.cinemakiosk.domain.PaymentDetailsEntity;
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

    /**
     * DTO -> Entity
     * @param couponDTO DTO
     * @return 변환을 위한 Builder
     */
    public static CouponEntity toEntity(CouponDTO couponDTO) {
        return CouponEntity.builder()
                .couponNum(couponDTO.getCouponNum())
                .discountPolicyEntity(DiscountPolicyDTO.toEntity(couponDTO.getDiscountPolicy()))
                .status(couponDTO.isStatus())
                .build();
    }

    /**
     * DTO -> VO
     * @param couponDTO DTO
     * @return 변환을 위한 Builder
     */
    public static CouponVO toVO(CouponDTO couponDTO) {
        return CouponVO.builder()
                .couponNum(couponDTO.getCouponNum())
                .discountPolicy(DiscountPolicyDTO.toVO(couponDTO.getDiscountPolicy()))
                .status(couponDTO.isStatus())
                .build();
    }
}
