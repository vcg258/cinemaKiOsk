package com.example.cinemakiosk.domain.PaymentDetailsEntity;

import com.example.cinemakiosk.domain.BonusPolicyEntity;
import com.example.cinemakiosk.domain.CouponEntity;
import com.example.cinemakiosk.domain.PointHistoryEntity.PointHistoryEntity;
import com.example.cinemakiosk.domain.ReservationDetailsEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Builder
@ToString(exclude = {"pointHistoryEntity", "reservationDetailsEntity", "couponEntity", "bonusPolicyEntity"})
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "payment_details")
public class PaymentDetailsEntity {
    @Id
    @Column(length = 36)
    private String id;             // 인덱스

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id",  foreignKey = @ForeignKey(name = "fk_payment_details_reservation_id"))
    private ReservationDetailsEntity reservationDetailsEntity;  // 예매 정보

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bonus_policy_id", foreignKey = @ForeignKey(name = "fk_payment_details_bonus_policy_id"))
    private BonusPolicyEntity bonusPolicyEntity;    // 사용한 적립 정책

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_num", foreignKey = @ForeignKey(name = "fk_payment_details_coupon_id"))
    private CouponEntity couponEntity;      // 사용한 할인 쿠폰, 없는 경우 null

    @Column(nullable = false)
    private Long cost;             // 결제 금액
    @Column(nullable = false)
    private LocalDateTime time;    // 결제 시간
    @Column(columnDefinition = "BIGINT UNSIGNED DEFAULT 0")
    private Long usePoint;         // 사용 포인트 기본값 0
    @Enumerated(EnumType.STRING)
    private Status status;         // ENUM ('PAY','RETURN','FAIL'), 결제 완료, 환불, 실패

    @OneToMany(mappedBy = "paymentDetailsEntity", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private  List<PointHistoryEntity> pointHistoryEntity;
}
