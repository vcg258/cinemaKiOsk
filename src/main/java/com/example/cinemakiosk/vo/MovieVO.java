package com.example.cinemakiosk.vo;

import com.example.cinemakiosk.domain.enums.Rating;
import com.example.cinemakiosk.dto.MovieDTO;
import com.example.cinemakiosk.dto.ScheduleDTO;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class MovieVO {

    private final Long movieId;         // 영화 인덱스
    private final String title;         // 영화 제목
    private final String genre;         // 장르
    private final Rating rating;        // 관람 등급
    private final Long runtime;         // 상영 시간 (분)
    private final String director;      // 감독
    private final String actors;        // 주연 배우
    private final String description;   // 줄거리
    private final LocalDate startAt; // 상영 시작일
    private final LocalDate endAt;   // 상영 종료일
    private final LocalDate createAt; // 등록일

    /**
     * VO -> DTO
     * @param movieVO VO
     * @return DTO
     */
    public static MovieDTO toDTO(MovieVO movieVO) {
        return MovieDTO.builder()
                .movieId(movieVO.getMovieId())
                .title(movieVO.getTitle())
                .genre(movieVO.getGenre())
                .rating(movieVO.getRating())
                .runtime(movieVO.getRuntime())
                .director(movieVO.getDirector())
                .actors(movieVO.getActors())
                .description(movieVO.getDescription())
                .startAt(movieVO.getStartAt())
                .endAt(movieVO.getEndAt())
                .createAt(movieVO.getCreateAt())
                .build();
    }
}