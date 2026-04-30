package com.example.cinemakiosk.domain;

import com.example.cinemakiosk.domain.enums.Status;
import com.example.cinemakiosk.dto.*;
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
@ToString(exclude = {"pointHistoryEntity", "reservationDetailsEntity", "couponEntity", "bonusPolicyEntity"})
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "payment_details")
public class PaymentDetailsEntity {
    @Id
    @Column(length = 36)
    private String id;             // 인덱스

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false, foreignKey = @ForeignKey(name = "fk_payment_details_reservation_id"))
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
    private LocalDateTime createAt;    // 결제 시간

    @Column(name = "payment_key", nullable = false, length = 100)
    private String paymentKey;    // 환불을 위한 키

    @Column(columnDefinition = "BIGINT UNSIGNED DEFAULT 0")
    private Long usePoint;         // 사용 포인트 기본값 0

    @Enumerated(EnumType.STRING)
    private Status status;         // ENUM ('PAY','RETURN','FAIL'), 결제 완료, 환불, 실패

    @OnDelete(action= OnDeleteAction.CASCADE)
    @OneToMany(mappedBy = "paymentDetailsEntity", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private  List<PointHistoryEntity> pointHistoryEntity;

    public void changeStatus(Status status) {
        this.status = status;
    }

    /**
     * Entity -> DTO
     * @param paymentDetailsEntity
     * @return DTO
     */
    public static PaymentDetailsDTO toDTO(PaymentDetailsEntity paymentDetailsEntity){

        return PaymentDetailsDTO.builder()
                .id(paymentDetailsEntity.getId())
                .reservation(ReservationDetailsEntity.toDTO(paymentDetailsEntity.getReservationDetailsEntity()))
                .bonusPolicy(BonusPolicyEntity.toDTO(paymentDetailsEntity.getBonusPolicyEntity()))
                .couponNum(paymentDetailsEntity.getCouponEntity() != null
                        ? CouponEntity.toDTO(paymentDetailsEntity.getCouponEntity())
                        : null)
                .cost(paymentDetailsEntity.getCost())
                .createAt(paymentDetailsEntity.getCreateAt())
                .usePoint(paymentDetailsEntity.getUsePoint())
                .status(paymentDetailsEntity.getStatus())
                .paymentKey(paymentDetailsEntity.getPaymentKey())
                .build();
    }
    
}
