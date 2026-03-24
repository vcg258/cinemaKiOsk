package com.example.cinemakiosk.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Arrays;

@Entity
@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "movie")
public class MovieEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movie_id", columnDefinition = "BIGINT UNSIGNED")
    private Long movieId;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "genre", length = 50)
    private String genre;

    @Column(name = "rating")
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

    @Column(name = "create_at", updatable = false)
    private LocalDateTime createAt;

}