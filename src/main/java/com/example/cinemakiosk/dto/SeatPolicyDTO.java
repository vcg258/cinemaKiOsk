package com.example.cinemakiosk.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SeatPolicyDTO {
    private String policyId; // 좌석 아이디
    private String name; // 좌석 이름
    private Long cost; // 좌석 비용
}
