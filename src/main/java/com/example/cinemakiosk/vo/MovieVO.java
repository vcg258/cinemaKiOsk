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

    private final Long movieId;         // 영화 인덱스
    private final String title;         // 영화 제목
    private final String genre;         // 장르
    private final Rating rating;        // 관람 등급
    private final Long runtime;         // 상영 시간 (분)
    private final String director;      // 감독
    private final String actors;        // 주연 배우
    private final String description;   // 줄거리
    private final LocalDateTime startAt; // 상영 시작일
    private final LocalDateTime endAt;   // 상영 종료일
    private final LocalDateTime createAt; // 등록일
}