package com.example.cinemakiosk.dto;

import com.example.cinemakiosk.domain.DiscountPolicyEntity.ConditionType;
import com.example.cinemakiosk.domain.DiscountPolicyEntity.DiscountPolicyEntity;
import com.example.cinemakiosk.domain.DiscountPolicyEntity.DiscountType;
import com.example.cinemakiosk.vo.CouponVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscountPolicyDTO {
    private Long id; // 할인 정책 인덱스
    private String policyName; // 정책이름
    private DiscountType discountType; // 할인 방식
    private Long discountValue; // 할인 값
    private ConditionType conditionType; // 할인 유형
    private LocalDateTime startAt; // 시작일
    private LocalDateTime endAt; // 만료일
    private boolean activation; // 활성화 여부
    private List<CouponVO> coupons; // resultMap(collection)


    /**
     * Entity -> DTO 변환
     * @param discountPolicyEntity Entity
     * @return DTO
     */
    public static DiscountPolicyDTO toDTO(DiscountPolicyEntity discountPolicyEntity) {
        return DiscountPolicyDTO.builder()
                .id(discountPolicyEntity.getId())
                .policyName(discountPolicyEntity.getPolicyName())
                .discountType(discountPolicyEntity.getDiscountType())
                .discountValue(discountPolicyEntity.getDiscountValue())
                .conditionType(discountPolicyEntity.getConditionType())
                .startAt(discountPolicyEntity.getStartAt())
                .endAt(discountPolicyEntity.getEndAt())
                .activation(discountPolicyEntity.isActivation())
                .build();
    }

    /**
     * DTO -> Entity
     * @param discountPolicyDTO DTO
     * @return Entity
     */
    public static DiscountPolicyEntity ToEntity(DiscountPolicyDTO discountPolicyDTO) {
        return DiscountPolicyEntity.builder()
                .id(discountPolicyDTO.getId())
                .policyName(discountPolicyDTO.getPolicyName())
                .discountType(discountPolicyDTO.getDiscountType())
                .discountValue(discountPolicyDTO.getDiscountValue())
                .conditionType(discountPolicyDTO.getConditionType())
                .startAt(discountPolicyDTO.getStartAt())
                .endAt(discountPolicyDTO.getEndAt())
                .activation(discountPolicyDTO.isActivation())
                .build();
    }
}
