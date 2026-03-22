package com.example.cinemakiosk.vo;

import com.example.cinemakiosk.domain.DiscountPolicy.ConditionType;
import com.example.cinemakiosk.domain.DiscountPolicy.DiscountType;
import lombok.*;

import java.time.LocalDateTime;

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
}
