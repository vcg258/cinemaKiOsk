package com.example.cinemakiosk.domain.DiscountPolicyEntity;

import com.example.cinemakiosk.domain.CouponEntity;
import com.example.cinemakiosk.dto.CouponDTO;
import com.example.cinemakiosk.dto.DiscountPolicyDTO;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@ToString(exclude = "coupon")
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "discount_policy")
public class DiscountPolicyEntity {
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
        this.activation = activation;
    }


    /**
     * Entity -> DTO 변환
     * @param discountPolicyEntity
     * @return DTO
     */
    public static DiscountPolicyDTO toDTO(DiscountPolicyEntity discountPolicyEntity) {
        //OneToMany 변수는 본인 객체를 제외한 값만 받기. 순환참조 방지.
        List<CouponEntity> couponEntities = discountPolicyEntity.getCoupons();
        List<CouponDTO> couponDTOs = new ArrayList<>();


        for (CouponEntity coupon : couponEntities){
            //pk 만 받아오기.
            CouponDTO couponDTO = CouponDTO.builder()
                    .couponNum(coupon.getCouponNum())
                    .build();

            couponDTOs.add(couponDTO);
        }

        return DiscountPolicyDTO.builder()
                .id(discountPolicyEntity.getId())
                .policyName(discountPolicyEntity.getPolicyName())
                .discountType(discountPolicyEntity.getDiscountType())
                .discountValue(discountPolicyEntity.getDiscountValue())
                .conditionType(discountPolicyEntity.getConditionType())
                .startAt(discountPolicyEntity.getStartAt())
                .endAt(discountPolicyEntity.getEndAt())
                .activation(discountPolicyEntity.isActivation())
                .coupons(couponDTOs)
                .build();
    }
}
