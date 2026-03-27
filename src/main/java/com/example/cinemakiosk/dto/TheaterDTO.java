package com.example.cinemakiosk.dto;

import com.example.cinemakiosk.domain.SeatPolicyEntity;
import com.example.cinemakiosk.vo.SeatPolicyVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TheaterDTO {
    private Long no; // 상영관 번호
    private String policyId; // 좌석 정책 FK
    private Long cleanupTime; // 정리시간(분)
    private SeatPolicyDTO seatPolicyDTO;
}
