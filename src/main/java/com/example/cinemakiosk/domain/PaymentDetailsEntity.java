package com.example.cinemakiosk.domain;

import com.example.cinemakiosk.domain.enums.Status;
import com.example.cinemakiosk.dto.*;
import jakarta.persistence.*;
import lombok.*;

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
    private LocalDateTime time;    // 결제 시간

    @Column(columnDefinition = "BIGINT UNSIGNED DEFAULT 0")
    private Long usePoint;         // 사용 포인트 기본값 0

    @Enumerated(EnumType.STRING)
    private Status status;         // ENUM ('PAY','RETURN','FAIL'), 결제 완료, 환불, 실패

    @OneToMany(mappedBy = "paymentDetailsEntity", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private  List<PointHistoryEntity> pointHistoryEntity;

    /**
     * Entity -> DTO
     * @param paymentDetailsEntity
     * @return DTO
     */
    public static PaymentDetailsDTO toDTO(PaymentDetailsEntity paymentDetailsEntity){
        //OneToMany 변수는 본인 객체를 제외한 값만 받기. 순환참조 방지.
        List<PointHistoryEntity> pointHistoryEntitys = paymentDetailsEntity.getPointHistoryEntity();
        List<PointHistoryDTO> pointHistoryDTOs = new ArrayList<>();


        for (PointHistoryEntity pointHistoryEntity : pointHistoryEntitys){
            //pk 만 받아오기.
            PointHistoryDTO pointHistoryDTO = PointHistoryDTO.builder()
                    .pointId(pointHistoryEntity.getPointId())
                    .build();

            pointHistoryDTOs.add(pointHistoryDTO);
        }

        return PaymentDetailsDTO.builder()
                .id(paymentDetailsEntity.getId())
                .reservation(ReservationDetailsEntity.toDTO(paymentDetailsEntity.getReservationDetailsEntity()))
                .bonusPolicy(BonusPolicyEntity.toDTO(paymentDetailsEntity.getBonusPolicyEntity()))
                .couponNum(CouponEntity.toDTO(paymentDetailsEntity.getCouponEntity()))
                .cost(paymentDetailsEntity.getCost())
                .time(paymentDetailsEntity.getTime())
                .usePoint(paymentDetailsEntity.getUsePoint())
                .status(paymentDetailsEntity.getStatus())
                .pointHistories(pointHistoryDTOs)
                .build();
    }
    
}
