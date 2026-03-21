package com.example.cinemakiosk.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@ToString (exclude = {"theater"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Schedule {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED")
    @Id private Long id; // 스케줄 인덱스
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "no", nullable = false, columnDefinition = "BIGINT UNSIGNED", foreignKey = @ForeignKey(name = "fk_schedule_theater_no"))
    private Theater theater; // 상영관 번호 FK

    // TODO (Long -> Movie)
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "movie_id", columnDefinition = "BIGINT UNSIGNED", foreignKey = @ForeignKey(name = "fk_schedule_movie_id"))
    @Column(name = "movie_id", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    private Long movie; // 영화 번호 FK
//    @Column(columnDefinition = "DATETIME DEFAULT NOW()")
//    private LocalDateTime startAt; // 상영 시작 시간
    private LocalDateTime endAt; // 상영 종료 시간

}
