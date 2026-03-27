package com.example.cinemakiosk.dto;

import com.example.cinemakiosk.domain.CouponEntity;
import com.example.cinemakiosk.domain.DiscountPolicyEntity;
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
    private DiscountPolicyDTO discountPolicy; // 할인 정책 인덱스 FK (JPA용도)
    private boolean status; // 사용여부 (사용가능 = true, 불가능 = false)
    private Long policyId; // 할인 정책 인덱스 FK (Mapper)

    /**
     * DTO -> Entity
     * @param couponDTO DTO
     * @return 변환을 위한 Builder
     */
    public static CouponEntity toEntity(CouponDTO couponDTO) {

        // discountPolicy 객체가 있으면 그걸 사용, 없으면 policyId로 Entity 생성
        DiscountPolicyEntity discountPolicyEntity = null;
        if (couponDTO.getDiscountPolicy() != null) {
            discountPolicyEntity = DiscountPolicyDTO.toEntity(couponDTO.getDiscountPolicy());
        } else if (couponDTO.getPolicyId() != null) {
            discountPolicyEntity = DiscountPolicyEntity.builder()
                    .id(couponDTO.getPolicyId())
                    .build();
        }

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
                .policyId(couponDTO.getPolicyId())
                .status(couponDTO.isStatus())
                .build();
    }
}
