package com.example.cinemakiosk.dto;

import com.example.cinemakiosk.domain.MovieEntity.MovieEntity;
import com.example.cinemakiosk.domain.MovieEntity.Rating;
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
    private Rating rating;      // ALL / 12 / 15 / 19
    private Long runtime;
    private String director;
    private String actors;
    private String description;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private LocalDateTime createAt;

    // Entity → DTO 변환
    public static MovieDTO toDTO(MovieEntity entity) {
        return MovieDTO.builder()
                .movieId(entity.getMovieId())
                .title(entity.getTitle())
                .genre(entity.getGenre())
                .rating(entity.getRating().toString())
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
    public static MovieEntity toEntity(MovieDTO movieDTO) {

        Rating ratingInput = null;
        for (Rating rating : Rating.values()) {
            if (rating.getConversion().equals(movieDTO.getRating())){
                ratingInput = rating;
            }

        }

        return MovieEntity.builder()
                .title(movieDTO.getTitle())
                .genre(movieDTO.getGenre())
                .rating(ratingInput)
                .runtime(movieDTO.getRuntime())
                .director(movieDTO.getDirector())
                .actors(movieDTO.getActors())
                .description(movieDTO.getDescription())
                .startAt(movieDTO.getStartAt())
                .endAt(movieDTO.getEndAt())
                .createAt(LocalDateTime.now())
                .build();
    }
}

