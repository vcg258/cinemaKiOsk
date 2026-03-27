package com.example.cinemakiosk.domain.MovieEntity;

import com.example.cinemakiosk.domain.ScheduleEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
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

    private String rating;

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

    @CreatedDate
    @Column(nullable = false, updatable = false, columnDefinition = "DATETIME DEFAULT NOW()")
    private LocalDateTime createAt;

    @OneToMany(mappedBy = "movieEntity", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<ScheduleEntity> scheduleEntity;
}