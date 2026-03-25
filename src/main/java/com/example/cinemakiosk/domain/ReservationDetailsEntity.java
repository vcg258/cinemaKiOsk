package com.example.cinemakiosk.domain;

import com.example.cinemakiosk.domain.PaymentDetailsEntity.PaymentDetailsEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Builder
@ToString(exclude = {"reservationSeatEntity", "scheduleEntity", "memberEntity"})
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "reservation_details")
public class ReservationDetailsEntity {
    @Id
    @Column(length = 36)
    private String id;                     // 예매 고유번호(uuid)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", columnDefinition = "BIGINT UNSIGNED", nullable = false, foreignKey = @ForeignKey(name = "fk_reservation_details_payment_id"))
    private ScheduleEntity scheduleEntity;               //  스케쥴 정보 FK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "phone", foreignKey = @ForeignKey(name = "fk_reservation_details_schedule_id"))
    private MemberEntity memberEntity;                  //  회원 번호 FK

    @Column(nullable = false)
    private LocalDateTime createAt; //  예약 시간

    @OneToMany(mappedBy = "reservationDetailsEntity", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<ReservationSeatEntity> reservationSeatEntity;

    @OneToMany(mappedBy = "reservationDetailsEntity", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<PaymentDetailsEntity> paymentDetailsEntity;
}
