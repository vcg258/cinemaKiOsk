package com.example.cinemakiosk.domain;

import com.example.cinemakiosk.domain.enums.Rating;
import com.example.cinemakiosk.domain.enums.RatingConverter;
import com.example.cinemakiosk.dto.MovieDTO;
import com.example.cinemakiosk.dto.ScheduleDTO;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@ToString(exclude = {"scheduleEntity"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "movie")
@Setter
public class MovieEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movie_id", columnDefinition = "BIGINT UNSIGNED")
    private Long movieId;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "genre", length = 50)
    private String genre;

    @Convert(converter = RatingConverter.class)
    private Rating rating;

    @Column(name = "runtime", columnDefinition = "BIGINT UNSIGNED", nullable = false)
    private Long runtime;

    @Column(name = "director", length = 50, nullable = false)
    private String director;

    @Column(name = "actors", length = 255)
    private String actors;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt;

    @Column(name = "end_at")
    private LocalDateTime endAt;

    @Column(name = "create_at", updatable = false)
    private LocalDateTime createAt;

    @OnDelete(action= OnDeleteAction.CASCADE)
    @OneToMany(mappedBy = "movieEntity", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<ScheduleEntity> scheduleEntity;


    // 수정 메서드
    public void update(MovieDTO dto) {
        this.title = dto.getTitle();
        this.genre = dto.getGenre();
        this.rating = Rating.fromConversion(dto.getRating());
        this.runtime = dto.getRuntime();
        this.director = dto.getDirector();
        this.actors = dto.getActors();
        this.description = dto.getDescription();
        this.startAt = dto.getStartAt();
        this.endAt = dto.getEndAt();
        this.createAt = dto.getCreateAt();
    }




    /**
     * Entity -> DTO
     * @param movieEntity
     * @return DTO
     */
    public static MovieDTO toDTO(MovieEntity movieEntity) {

        return MovieDTO.builder()
                .movieId(movieEntity.getMovieId())
                .title(movieEntity.getTitle())
                .genre(movieEntity.getGenre())
                .rating(movieEntity.getRating().getConversion())
                .runtime(movieEntity.getRuntime())
                .director(movieEntity.getDirector())
                .actors(movieEntity.getActors())
                .description(movieEntity.getDescription())
                .startAt(movieEntity.getStartAt())
                .endAt(movieEntity.getEndAt())
                .createAt(movieEntity.getCreateAt())
                .build();
    }
}