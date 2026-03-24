package com.example.cinemakiosk.vo;

import com.example.cinemakiosk.dto.ReservationDetailsDTO;
import com.example.cinemakiosk.dto.ReservationSeatDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.example.cinemakiosk.vo.ScheduleVO;
import com.example.cinemakiosk.vo.ReservationSeatVO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReservationDetailsVO {
    private String id;                     // 예매 고유번호(uuid)
    private ScheduleVO schedule;           //  스케쥴 정보
    private String phone;                  //  회원 번호
    private LocalDateTime reservationTime; //  예약 시간
    private List<ReservationSeatVO> seats; //  예매한 좌석들의 정보


    public static ReservationDetailsDTO toDTO(ReservationDetailsVO reservationDetailsVO) {

        List<ReservationSeatDTO> seats = new ArrayList<>();

        for (var seat : reservationDetailsVO.getSeats()) {
            seats.add(ReservationSeatVO.toDTO(seat));
        }


        return ReservationDetailsDTO.builder()
                .id(reservationDetailsVO.getId())
                .schedule(ScheduleVO.toDTO(reservationDetailsVO.getSchedule()))
                .phone(reservationDetailsVO.getPhone())
                .reservationTime(reservationDetailsVO.getReservationTime())
                .seats(seats)
                .build();
    }
}
