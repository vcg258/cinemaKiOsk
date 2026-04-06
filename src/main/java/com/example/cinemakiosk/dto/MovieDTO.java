package com.example.cinemakiosk.dto;

import com.example.cinemakiosk.domain.MovieEntity;
import com.example.cinemakiosk.domain.enums.Rating;
import com.example.cinemakiosk.vo.MovieVO;
import lombok.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class MovieDTO {
    private Long movieId;
    @NotNull
    private String title;

    private String genre;
    private Rating rating;      // ALL / 12 / 15 / 19
    @NotNull
    private Long runtime;

    private String director;
    private String actors;
    private String description;
    @NotNull
    private LocalDate startAt;

    private LocalDate endAt;
    private LocalDate createAt;
    private MultipartFile image;
    private String posterPath;


    /**
     * DTO -> Entity
     * @param movieDTO DTO
     * @return Entity
     */
    public static MovieEntity toEntity(MovieDTO movieDTO) {

        return MovieEntity.builder()
                .movieId(movieDTO.getMovieId())
                .title(movieDTO.getTitle())
                .genre(movieDTO.getGenre())
                .rating(movieDTO.getRating())
                .runtime(movieDTO.getRuntime())
                .director(movieDTO.getDirector())
                .actors(movieDTO.getActors())
                .description(movieDTO.getDescription())
                .startAt(movieDTO.getStartAt())
                .endAt(movieDTO.getEndAt())
                .createAt(movieDTO.getCreateAt())
                .build();
    }
    /**
     * DTO -> VO
     * @param movieDTO DTO
     * @return VO
     */
    public static MovieVO toVO(MovieDTO movieDTO) {
        return MovieVO.builder()
                .movieId(movieDTO.getMovieId())
                .title(movieDTO.getTitle())
                .genre(movieDTO.getGenre())
                .rating(movieDTO.getRating())
                .runtime(movieDTO.getRuntime())
                .director(movieDTO.getDirector())
                .actors(movieDTO.getActors())
                .description(movieDTO.getDescription())
                .startAt(movieDTO.getStartAt())
                .endAt(movieDTO.getEndAt())
                .createAt(movieDTO.getCreateAt())
                .build();
    }
}

