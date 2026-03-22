package com.example.cinemakiosk.vo;

import lombok.*;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SeatPolicyVO {
    private String policyId; // 좌석 아이디
    private String name; // 좌석 이름
    private Long cost; // 좌석 비용
}
