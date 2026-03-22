package com.example.cinemakiosk.domain.DiscountPolicy;

import com.example.cinemakiosk.domain.Coupon;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Builder
@ToString(exclude = "coupon")
@NoArgsConstructor
@AllArgsConstructor
public class DiscountPolicy {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED")
    @Id private Long id; // 할인 정책 인덱스
    @Column(length = 20)
    private String policyName; // 정책이름
    @Enumerated(EnumType.STRING)
    private DiscountType discountType; // 할인 방식
    @Column(columnDefinition = "BIGINT UNSIGNED")
    private Long discountValue; // 할인 값
    @Enumerated(EnumType.STRING)
    private ConditionType conditionType; // 할인 유형
    @Column(columnDefinition = "DATETIME DEFAULT NOW()")
    private LocalDateTime startAt; // 시작일
    private LocalDateTime endAt; // 만료일
    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean activation; // 활성화 여부

    @OneToMany(mappedBy = "discountPolicy", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<Coupon> coupon;
}
