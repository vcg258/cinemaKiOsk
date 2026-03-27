package com.example.cinemakiosk.dto;

import com.example.cinemakiosk.domain.ReservationDetailsEntity;
import com.example.cinemakiosk.domain.ReservationSeatEntity;
import com.example.cinemakiosk.vo.ReservationDetailsVO;
import com.example.cinemakiosk.vo.ReservationSeatVO;
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
    private ReservationDetailsDTO reservationDetails;  //예매 내역 아이디
    private String seatNumber;     //좌석 번호

    /**
     * DTO -> Entity
     * @param reservationSeatDTO
     * @return Entity
     */
    public static ReservationSeatEntity toEntity(ReservationSeatDTO reservationSeatDTO){
        return ReservationSeatEntity.builder()
                .id(reservationSeatDTO.getId())
                .reservationDetailsEntity(ReservationDetailsDTO.toEntity(reservationSeatDTO.getReservationDetails()))
                .seatNumber(reservationSeatDTO.getSeatNumber())
                .build();
    }

    /**
     * DTO -> VO
     * @param reservationSeatDTO
     * @return VO
     */
    public static ReservationSeatVO toVO(ReservationSeatDTO reservationSeatDTO){
        return ReservationSeatVO.builder()
                .id(reservationSeatDTO.getId())
                .reservationDetails(ReservationDetailsDTO.toVO(reservationSeatDTO.getReservationDetails()))
                .seatNumber(reservationSeatDTO.getSeatNumber())
                .build();
    }
}
