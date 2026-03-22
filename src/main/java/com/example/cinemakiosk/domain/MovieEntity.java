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
    @Column(name = "movie_id")
    private Long movieId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "genre")
    private String genre;

    @Convert(converter = MovieEntity.RatingConverter.class)
    @Column(name = "rating")
    private Rating rating;

    @Column(name = "runtime")
    private Long runtime;

    @Column(name = "director")
    private String director;

    @Column(name = "actors")
    private String actors;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "start_at")
    private LocalDateTime startAt;

    @Column(name = "end_at")
    private LocalDateTime endAt;

    @Column(name = "create_at", updatable = false)
    private LocalDateTime createAt;


    // Rating ENUM
    @Getter
    @RequiredArgsConstructor
    public enum Rating {
        ALL("ALL"),
        TWELVE("12"),
        FIFTEEN("15"),
        NINETEEN("19");

        private final String value;  // DB에 저장될 실제 값
    }


    // RatingConverter
    @Converter
    public static class RatingConverter implements AttributeConverter<Rating, String> {

        // Java → DB (저장 시)
        @Override
        public String convertToDatabaseColumn(Rating rating) {
            if (rating == null) return null;
            return rating.getValue();   // TWELVE → "12"
        }

        // DB → Java (조회 시)
        @Override
        public Rating convertToEntityAttribute(String dbData) {
            if (dbData == null) return null;
            return Arrays.stream(Rating.values())
                    .filter(r -> r.getValue().equals(dbData))  // "12"인 ENUM 탐색
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("알 수 없는 등급 값: " + dbData));
        }
    }
}