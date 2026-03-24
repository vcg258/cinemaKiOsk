package com.example.cinemakiosk.domain;

import com.example.cinemakiosk.domain.DiscountPolicy.DiscountPolicy;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@ToString (exclude = "discountPolicy")
@NoArgsConstructor
@AllArgsConstructor
public class Coupon {
    @Column(length = 12)
    @Id private String couponNum; // 쿠폰 번호
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", nullable = false, columnDefinition = "BIGINT UNSIGNED", foreignKey = @ForeignKey(name = "fk_discount_policy_coupon_id"))
    private DiscountPolicy discountPolicy; // 할인 정책 인덱스 FK
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean status; // 사용여부 (사용가능 = true, 불가능 = false)

    /**
     * 쿠폰 사용여부 변환 메서드
     * @param status 변경할 상태값
     */
    public void changeStatus(boolean status) {
        this.status = status;
    }
}
