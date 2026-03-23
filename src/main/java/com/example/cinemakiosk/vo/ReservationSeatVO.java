package com.example.cinemakiosk.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReservationSeatVO {
    private Long id;               //인덱스
    private String reservationId;  //예매 내역 아이디
    private String seatNumber;     //좌석 번호
}
