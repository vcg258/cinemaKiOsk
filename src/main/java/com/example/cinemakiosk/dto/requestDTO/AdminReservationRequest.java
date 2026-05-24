package com.example.cinemakiosk.dto.requestDTO;

import lombok.Data;

import java.util.List;

@Data
public class AdminReservationRequest {
    private Long scheduleId;       // 예매할 스케줄 ID
    private List<String> seats;    // 선택한 좌석 번호 목록 (예: ["A1", "A2"])
}
