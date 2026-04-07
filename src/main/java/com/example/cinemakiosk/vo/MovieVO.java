package com.example.cinemakiosk.vo;

import com.example.cinemakiosk.domain.enums.Rating;
import com.example.cinemakiosk.dto.MovieDTO;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MovieVO {
    private Long movieId;         // 영화 인덱스
    private String title;         // 영화 제목
    private String genre;         // 장르
    private Rating rating;        // 관람 등급
    private Long runtime;         // 상영 시간 (분)
    private String director;      // 감독
    private String actors;        // 주연 배우
    private String description;   // 줄거리
    private LocalDate startAt; // 상영 시작일
    private LocalDateTime endAt;   // 상영 종료일
    private LocalDate createAt; // 등록일

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