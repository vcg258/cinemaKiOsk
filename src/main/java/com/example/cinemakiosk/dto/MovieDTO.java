package com.example.cinemakiosk.dto;

import com.example.cinemakiosk.domain.MovieEntity;
import com.example.cinemakiosk.domain.Rating;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class MovieDTO {


    private Long movieId;
    private String title;
    private String genre;
    private String rating;      // ALL / 12 / 15 / 19
    private Long runtime;
    private String director;
    private String actors;
    private String description;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private LocalDateTime createAt;

    // Entity → DTO 변환
    public static MovieDTO from(MovieEntity entity) {
        return MovieDTO.builder()
                .movieId(entity.getMovieId())
                .title(entity.getTitle())
                .genre(entity.getGenre())
                .rating(entity.getRating())
                .runtime(entity.getRuntime())
                .director(entity.getDirector())
                .actors(entity.getActors())
                .description(entity.getDescription())
                .startAt(entity.getStartAt())
                .endAt(entity.getEndAt())
                .createAt(entity.getCreateAt())
                .build();
    }

    // DTO → Entity 변환
    public MovieEntity toEntity() {
        return MovieEntity.builder()
                .title(this.title)
                .genre(this.genre)
                .rating(this.rating)
                .runtime(this.runtime)
                .director(this.director)
                .actors(this.actors)
                .description(this.description)
                .startAt(this.startAt)
                .endAt(this.endAt)
                .createAt(LocalDateTime.now())
                .build();
    }
}