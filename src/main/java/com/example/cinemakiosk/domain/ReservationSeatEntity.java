package com.example.cinemakiosk.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "reservation_seat")
public class ReservationSeatEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;               //인덱스

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false, foreignKey = @ForeignKey(name = "fk_reservation_seat_reservation_id"))
    private ReservationDetailsEntity reservationDetailsEntity;  //예매 내역 아이디

    @Column(length = 10, nullable = false)
    private String seatNumber;     //좌석 번호
}
