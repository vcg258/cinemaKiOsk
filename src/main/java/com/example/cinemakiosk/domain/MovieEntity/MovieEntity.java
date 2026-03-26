package com.example.cinemakiosk.domain.MovieEntity;

import com.example.cinemakiosk.domain.ScheduleEntity;
import com.example.cinemakiosk.domain.TimeBaseEntity;
import com.example.cinemakiosk.dto.MovieDTO;
import com.example.cinemakiosk.dto.ScheduleDTO;
import jakarta.persistence.*;
import lombok.*;

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
public class MovieEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movie_id", columnDefinition = "BIGINT UNSIGNED")
    private Long movieId;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "genre", length = 50)
    private String genre;

    @Enumerated(EnumType.STRING)
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

    @OneToMany(mappedBy = "movieEntity", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<ScheduleEntity> scheduleEntity;

    /**
     * Entity -> DTO
     * @param movieEntity
     * @return DTO
     */
    public static MovieDTO toDTO(MovieEntity movieEntity) {
        //OneToMany 변수는 본인 객체를 제외한 값만 받기. 순환참조 방지.
        List<ScheduleEntity> scheduleEntities = movieEntity.getScheduleEntity();
        List<ScheduleDTO> scheduleDTOs = new ArrayList<>();


        for (ScheduleEntity schedule : scheduleEntities){
            //pk 만 받아오기.
            ScheduleDTO scheduleDTO = ScheduleDTO.builder()
                    .id(schedule.getId())
                    .build();

            scheduleDTOs.add(scheduleDTO);
        }

        return MovieDTO.builder()
                .movieId(movieEntity.getMovieId())
                .title(movieEntity.getTitle())
                .genre(movieEntity.getGenre())
                .rating(movieEntity.getRating())
                .runtime(movieEntity.getRuntime())
                .director(movieEntity.getDirector())
                .actors(movieEntity.getActors())
                .description(movieEntity.getDescription())
                .startAt(movieEntity.getStartAt())
                .endAt(movieEntity.getEndAt())
                .createAt(movieEntity.getCreateAt())
                .schedules(scheduleDTOs)
                .build();
    }
}