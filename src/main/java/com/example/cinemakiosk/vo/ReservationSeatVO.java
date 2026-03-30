package com.example.cinemakiosk.vo;

import com.example.cinemakiosk.dto.ReservationDetailsDTO;
import com.example.cinemakiosk.dto.ReservationSeatDTO;
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
    private String reservationDetailsId;  //예매 내역 아이디
    private String seatNumber;     //좌석 번호

    /**
     * VO -> DTO
     * @param reservationSeatVO
     * @return DTO
     */
    public static ReservationSeatDTO toDTO(ReservationSeatVO reservationSeatVO){
        return ReservationSeatDTO.builder()
                .id(reservationSeatVO.getId())
                .reservationDetailsId(reservationSeatVO.getReservationDetailsId())
                .seatNumber(reservationSeatVO.getSeatNumber())
                .build();
    }
}
