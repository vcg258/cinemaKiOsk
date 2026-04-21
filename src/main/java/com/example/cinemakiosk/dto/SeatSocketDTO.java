package com.example.cinemakiosk.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatSocketDTO {
    private String userId; //어떤 유저가 접속한 것인지
    private Long scheduleId;    // 어떤 영화 스케줄인지
    private List<String> seats; // 선택한 좌석 리스트
    private String action; //RESERVE(예약), RELEASE(해제), GET(정보요청)
}
