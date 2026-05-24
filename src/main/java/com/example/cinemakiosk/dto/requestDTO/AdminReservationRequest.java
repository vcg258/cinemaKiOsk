package com.example.cinemakiosk.dto.requestDTO;

import lombok.Data;

import java.util.List;

@Data
public class AdminReservationRequest {
    private Long scheduleId;       // 예매할 스케줄 ID
    private List<String> seats;    // 선택한 좌석 번호 목록 (예: ["A1", "A2"])
    private String phone;          // 회원 전화번호 (없으면 null → 비회원 처리)
}
