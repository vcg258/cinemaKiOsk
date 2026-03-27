package com.example.cinemakiosk.vo;

import com.example.cinemakiosk.domain.MovieEntity.MovieEntity;
import com.example.cinemakiosk.domain.MovieEntity.Rating;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class MovieVO {

    private Long movieId;         // 영화 인덱스
    private String title;         // 영화 제목
    private String genre;         // 장르
    private String rating;        // 관람 등급
    private Long runtime;         // 상영 시간 (분)
    private String director;      // 감독
    private String actors;        // 주연 배우
    private String description;   // 줄거리
    private LocalDateTime startAt; // 상영 시작일
    private LocalDateTime endAt;   // 상영 종료일
    private LocalDateTime createAt; // 등록일
}