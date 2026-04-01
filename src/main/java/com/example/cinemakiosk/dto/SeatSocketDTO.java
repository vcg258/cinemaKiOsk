package com.example.cinemakiosk.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatSocketDTO {
    private Long scheduleId;                // 어떤 영화 스케줄인지
    private List<ReservationSeatDTO> seats; // 선택한 좌석 리스트
}
