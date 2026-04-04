package com.example.cinemakiosk.vo;

import com.example.cinemakiosk.dto.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleVO {
    private Long id; // 스케줄 인덱스
    private Long no; // 상영관 정보
    private Long movieId; // 영화 번호 FK
    private LocalDateTime startAt; // 상영 시작 시간
    private LocalDateTime endAt; // 상영 종료 시간
    private boolean activation; // 활성화 여부 (유효 = True, 비활성화 = False)

    /**
     * VO -> DTO
     * @param scheduleVO
     * @return DTO
     */
    public static ScheduleDTO toDTO(ScheduleVO scheduleVO) {

        return ScheduleDTO.builder()
                .id(scheduleVO.getId())
                .no(scheduleVO.getNo())
                .movieId(scheduleVO.getMovieId())
                .startAt(scheduleVO.getStartAt())
                .endAt(scheduleVO.getEndAt())
                .activation(scheduleVO.isActivation())
                .build();
    }
}
