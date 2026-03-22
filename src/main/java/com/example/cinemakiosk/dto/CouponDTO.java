package com.example.cinemakiosk.dto;

import com.example.cinemakiosk.domain.Coupon;
import com.example.cinemakiosk.domain.DiscountPolicy.DiscountPolicy;
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
    private Long policyId; // 할인 정책 인덱스 FK
    private boolean status; // 사용여부 (사용가능 = true, 불가능 = false)




    /**
     * Entity -> DTO 변환
     * @param coupon Entity
     * @return 변환을 위한 Builder
     */
    public static CouponDTO ToDTO(Coupon coupon) {
        return CouponDTO.builder()
                .couponNum(coupon.getCouponNum())
                .status(coupon.isStatus())
                .policyId(coupon.getDiscountPolicy().getId())
                .build();
    }

    /**
     * DTO -> Entity 변환
     * @param couponDTO DTO
     * @param discountPolicy FK를 위한 Entity
     * @return 변환을 위한 Builder
     */
    public static Coupon fromDTO(CouponDTO couponDTO, DiscountPolicy discountPolicy) {
        return Coupon.builder()
                .couponNum(couponDTO.getCouponNum())
                .status(couponDTO.isStatus())
                .discountPolicy(discountPolicy)
                .build();
    }
}
