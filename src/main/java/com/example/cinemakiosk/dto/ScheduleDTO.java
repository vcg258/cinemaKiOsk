package com.example.cinemakiosk.dto;

import com.example.cinemakiosk.domain.*;
import com.example.cinemakiosk.vo.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleDTO {
    private Long id; // 스케줄 인덱스
    @JsonIgnore
    private TheaterDTO theater; // 상영관 정보 FK (JPA 전용)
    private Long no; // 상영관 FK
    @JsonIgnore
    private MovieDTO movie; // 영화 번호 FK (JPA 전용)
    private Long movieId; // 영화관 FK
    private LocalDateTime startAt; // 상영 시작 시간
    private LocalDateTime endAt; // 상영 종료 시간
    private boolean activation; // 활성화 여부 (유효 = True, 비활성화 = False)

    /**
     * DTO -> Entity
     * @param scheduleDTO
     * @return Entity
     */
    public static ScheduleEntity toEntity(ScheduleDTO scheduleDTO) {

        TheaterEntity theaterEntity = null;
        if (scheduleDTO.getTheater() != null) {
            theaterEntity = TheaterEntity.builder()
                    .no(scheduleDTO.getTheater().getNo())
                    .build();
        } else if (scheduleDTO.getNo() != null) {
            theaterEntity = TheaterEntity.builder()
                    .no(scheduleDTO.getNo())
                    .build();
        }

        MovieEntity movieEntity = null;
        if (scheduleDTO.getMovie() != null) {
            movieEntity = MovieEntity.builder()
                    .movieId(scheduleDTO.getMovie().getMovieId())
                    .build();
        } else if (scheduleDTO.getMovieId() != null) {
            movieEntity = MovieEntity.builder()
                    .movieId(scheduleDTO.getMovieId())
                    .build();
        }

        return ScheduleEntity.builder()
                .id(scheduleDTO.getId())
                .theaterEntity(theaterEntity)
                .movieEntity(movieEntity)
                .startAt(scheduleDTO.getStartAt())
                .endAt(scheduleDTO.getEndAt())
                .activation(scheduleDTO.isActivation())
                .build();
    }

    /**
     * DTO -> VO
     * @param scheduleDTO
     * @return VO
     */
    public static ScheduleVO toVO(ScheduleDTO scheduleDTO) {
        return ScheduleVO.builder()
                .id(scheduleDTO.getId())
                .no(scheduleDTO.getNo())
                .movieId(scheduleDTO.getMovieId())
                .startAt(scheduleDTO.getStartAt())
                .endAt(scheduleDTO.getEndAt())
                .activation(scheduleDTO.isActivation())
                .build();
    }
}
