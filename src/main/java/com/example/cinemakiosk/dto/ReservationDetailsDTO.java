package com.example.cinemakiosk.dto;

import com.example.cinemakiosk.vo.ScheduleDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReservationDetailsDTO {
    private String id;                     // 예매 고유번호(uuid)
    private ScheduleDTO schedule;           //  스케쥴 정보
    private String phone;                  //  회원 번호
    private LocalDateTime reservationTime; //  예약 시간
    private List<ReservationSeatDTO> seats; //  예매한 좌석들의 정보
}
