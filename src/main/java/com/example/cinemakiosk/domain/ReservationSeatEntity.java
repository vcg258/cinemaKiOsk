package com.example.cinemakiosk.domain;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReservationSeatEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;               //인덱스

    @Column(length = 36, nullable = false)
    private String reservationId;  //예매 내역 아이디

    @Column(length = 10, nullable = false)
    private String seatNumber;     //좌석 번호
}
