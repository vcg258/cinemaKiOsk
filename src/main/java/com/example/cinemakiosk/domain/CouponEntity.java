package com.example.cinemakiosk.domain;

import com.example.cinemakiosk.domain.DiscountPolicyEntity.DiscountPolicyEntity;
import com.example.cinemakiosk.domain.PaymentDetailsEntity.PaymentDetailsEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Builder
@ToString (exclude = "discountPolicyEntity")
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "coupon")
public class CouponEntity {
    @Column(length = 12)
    @Id
    private String couponNum; // 쿠폰 번호

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", nullable = false, columnDefinition = "BIGINT UNSIGNED", foreignKey = @ForeignKey(name = "fk_discount_policy_coupon_id"))
    private DiscountPolicyEntity discountPolicyEntity; // 할인 정책 인덱스 FK

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean status; // 사용여부 (사용가능 = true, 불가능 = false)

    @OneToOne(mappedBy = "couponEntity", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private PaymentDetailsEntity paymentDetailsEntity;

    /**
     * 쿠폰 사용여부 변환 메서드
     * @param status 변경할 상태값
     */
    public void changeStatus(boolean status) {
        this.status = status;
    }


}
