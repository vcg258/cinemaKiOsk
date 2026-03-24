package com.example.cinemakiosk.dto;

import com.example.cinemakiosk.vo.ScheduleVO;
import com.example.cinemakiosk.vo.TheaterVO;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleDTO {
    private Long id; // 스케줄 인덱스
    private TheaterVO theater; // 상영관 정보
    private Long movie_id; // 영화 번호 FK
    private LocalDateTime startAt; // 상영 시작 시간
    private LocalDateTime endAt; // 상영 종료 시간

    public static ScheduleVO toVO(ScheduleDTO scheduleDTO) {
        return ScheduleVO.builder()
                .id(scheduleDTO.getId())
                .theater(scheduleDTO.getTheater())
                .movie_id(scheduleDTO.getMovie_id())
                .startAt(scheduleDTO.getStartAt())
                .endAt(scheduleDTO.getEndAt())
                .build();
    }
}
