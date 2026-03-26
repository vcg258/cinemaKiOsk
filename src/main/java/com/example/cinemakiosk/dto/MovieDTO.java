package com.example.cinemakiosk.dto;

import com.example.cinemakiosk.domain.MovieEntity.MovieEntity;
import com.example.cinemakiosk.domain.MovieEntity.Rating;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

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

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startAt;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endAt;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createAt;

    private MultipartFile image;




    // Entity → DTO 변환
    public static MovieDTO toDTO(MovieEntity entity) {
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
    public static MovieEntity toEntity(MovieDTO movieDTO) {

        return MovieEntity.builder()
                .title(movieDTO.getTitle())
                .genre(movieDTO.getGenre())
                .rating(movieDTO.getRating())
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

