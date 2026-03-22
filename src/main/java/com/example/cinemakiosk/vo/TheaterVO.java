package com.example.cinemakiosk.vo;

import lombok.*;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TheaterVO {
    private Long no; // 상영관 번호
    private String policyId; // 좌석 정책 FK
    private Long cleanupTime; // 정리시간(분)
}
