package com.example.cinemakiosk.domain;

import com.example.cinemakiosk.domain.enums.ConditionType;
import com.example.cinemakiosk.domain.enums.DiscountType;
import com.example.cinemakiosk.dto.CouponDTO;
import com.example.cinemakiosk.dto.DiscountPolicyDTO;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@ToString(exclude = "coupons")
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "discount_policy")
public class DiscountPolicyEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED")
    @Id private Long id; // 할인 정책 인덱스

    @Column(length = 20, nullable = false)
    private String policyName; // 정책이름

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiscountType discountType; // 할인 방식

    @Column(nullable = false, columnDefinition = "BIGINT UNSIGNED")
    private Long discountValue; // 할인 값

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConditionType conditionType; // 할인 유형

    @Column(nullable = false, columnDefinition = "DATETIME DEFAULT NOW()")
    private LocalDateTime startAt; // 시작일

    private LocalDateTime endAt; // 만료일

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean activation; // 활성화 여부

    @OnDelete(action= OnDeleteAction.CASCADE)
    @OneToMany(mappedBy = "discountPolicyEntity", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<CouponEntity> coupons;

    /**
     * 할인 정책 만료일 변경 메서드
     * @param endAt 변경할 만료일
     */
    public void finalDiscountPolicy(LocalDateTime endAt) {
        this.endAt = endAt;
    }

    /**
     * 할인 정책 활성화 ON / OFF (만든김에 버튼하나 딸깍 누르면 변경될거 생각해서 만들어봄)
     */
    public void changeActivation(boolean activation) {
        if (this.activation == activation) {
            return;
        }
        this.activation = activation;
    }


    /**
     * Entity -> DTO 변환
     * @param discountPolicyEntity
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
}
