package com.example.cinemakiosk.dto;

import com.example.cinemakiosk.vo.ReservationDetailsVO;
import com.example.cinemakiosk.vo.ReservationSeatVO;
import com.example.cinemakiosk.vo.ScheduleVO;
import com.example.cinemakiosk.dto.ScheduleDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReservationDetailsDTO {
    private String id;                     // 예매 고유번호(uuid)
    private ScheduleDTO schedule;           //  스케쥴 정보
    private MemberDTO phone;                  //  회원 번호
    private LocalDateTime reservationTime; //  예약 시간
    private List<ReservationSeatDTO> seats; //  예매한 좌석들의 정보

    public static ReservationDetailsVO toVO(ReservationDetailsDTO reservationDetailsDTO) {

        List<ReservationSeatVO> seats = new ArrayList<>();

        for (var seat : reservationDetailsDTO.getSeats()) {
            seats.add(ReservationSeatDTO.toVO(seat));
        }


        return ReservationDetailsVO.builder()
                .id(reservationDetailsDTO.getId())
                .schedule(ScheduleDTO.toVO(reservationDetailsDTO.getSchedule()))
                .phone(String.valueOf(reservationDetailsDTO.getPhone())) // TODO
                .reservationTime(reservationDetailsDTO.getReservationTime())
                .seats(seats)
                .build();
    }

}
