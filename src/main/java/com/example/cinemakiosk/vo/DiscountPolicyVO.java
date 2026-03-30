package com.example.cinemakiosk.vo;

import com.example.cinemakiosk.domain.enums.ConditionType;
import com.example.cinemakiosk.domain.enums.DiscountType;
import com.example.cinemakiosk.dto.CouponDTO;
import com.example.cinemakiosk.dto.DiscountPolicyDTO;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DiscountPolicyVO {
    private Long id; // 할인 정책 인덱스
    private String policyName; // 정책이름
    private DiscountType discountType; // 할인 방식
    private Long discountValue; // 할인 값
    private ConditionType conditionType; // 할인 유형
    private LocalDateTime startAt; // 시작일
    private LocalDateTime endAt; // 만료일
    private boolean activation; // 활성화 여부

    /**
     * VO -> DTO
     * @param discountPolicyVO VO
     * @return DTO
     */
    public static DiscountPolicyDTO toDTO(DiscountPolicyVO discountPolicyVO) {
        return DiscountPolicyDTO.builder()
                .id(discountPolicyVO.getId())
                .policyName(discountPolicyVO.getPolicyName())
                .discountType(discountPolicyVO.getDiscountType())
                .discountValue(discountPolicyVO.getDiscountValue())
                .conditionType(discountPolicyVO.getConditionType())
                .startAt(discountPolicyVO.getStartAt())
                .endAt(discountPolicyVO.getEndAt())
                .activation(discountPolicyVO.isActivation())
                .build();
    }
}
