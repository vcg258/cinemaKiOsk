package com.example.cinemakiosk.dto;

import com.example.cinemakiosk.domain.CouponEntity;
import com.example.cinemakiosk.domain.DiscountPolicyEntity.DiscountPolicyEntity;
import com.example.cinemakiosk.vo.CouponVO;
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
    private DiscountPolicyDTO policyId; // 할인 정책 인덱스 FK
    private boolean status; // 사용여부 (사용가능 = true, 불가능 = false)

    /**
     * Entity -> DTO 변환
     * @param couponEntity Entity
     * @return 변환을 위한 Builder
     */
    public static CouponDTO ToDTO(CouponEntity couponEntity) {
        return CouponDTO.builder()
                .couponNum(couponEntity.getCouponNum())
                .status(couponEntity.isStatus())
                .policyId(couponEntity.getDiscountPolicyEntity().getId())
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
                .status(couponDTO.isStatus())
                .policyId(couponDTO.getPolicyId())
                .build();
    }

    /**
     * DTO -> Entity 변환
     * @param couponDTO DTO
     * @param discountPolicyEntity FK를 위한 Entity (JPA는 인식을 못하기때문에 잡아줌)
     * @return 변환을 위한 Builder
     */
    public static CouponEntity toEntity(CouponDTO couponDTO, DiscountPolicyEntity discountPolicyEntity) {
        return CouponEntity.builder()
                .couponNum(couponDTO.getCouponNum())
                .status(couponDTO.isStatus())
                .discountPolicyEntity(discountPolicyEntity)
                .build();
    }
}
