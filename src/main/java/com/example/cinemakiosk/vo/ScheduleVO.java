package com.example.cinemakiosk.vo;

import com.example.cinemakiosk.dto.ScheduleDTO;
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

    public static ScheduleDTO toDTO(ScheduleVO scheduleVO) {
        return ScheduleDTO.builder()
                .id(scheduleVO.getId())
                .theater(scheduleVO.getTheater())
                .movie_id(scheduleVO.getMovie_id())
                .startAt(scheduleVO.getStartAt())
                .endAt(scheduleVO.getEndAt())
                .build();
    }
}
