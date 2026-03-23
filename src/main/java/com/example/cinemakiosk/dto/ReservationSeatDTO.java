package com.example.cinemakiosk.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReservationSeatDTO {
    private Long id;               //인덱스
    private String reservationId;  //예매 내역 아이디
    private String seatNumber;     //좌석 번호
}
