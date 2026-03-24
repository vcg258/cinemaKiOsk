package com.example.cinemakiosk.vo;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleVO {
    private Long id; // 스케줄 인덱스
    private TheaterVO theater; // 상영관 정보
    private Long movie_id; // 영화 번호 FK
    private LocalDateTime startAt; // 상영 시작 시간
    private LocalDateTime endAt; // 상영 종료 시간
}
