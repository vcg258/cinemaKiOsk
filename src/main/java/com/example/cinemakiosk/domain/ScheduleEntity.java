package com.example.cinemakiosk.domain;

import com.example.cinemakiosk.dto.*;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@ToString (exclude = {"theaterEntity", "reservationDetailsEntity", "movieEntity", "statisticsEntity"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "schedule")
public class ScheduleEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED")
    @Id
    private Long id; // 스케줄 인덱스

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "no", nullable = false, columnDefinition = "BIGINT UNSIGNED", foreignKey = @ForeignKey(name = "fk_schedule_theater_no"))
    private TheaterEntity theaterEntity; // 상영관 번호 FK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", columnDefinition = "BIGINT UNSIGNED", foreignKey = @ForeignKey(name = "fk_schedule_movie_id"))
    private MovieEntity movieEntity; // 영화 번호 FK

    @Column(columnDefinition = "DATETIME DEFAULT NOW()")
    private LocalDateTime startAt; // 상영 시작 시간
    private LocalDateTime endAt; // 상영 종료 시간

    @OnDelete(action= OnDeleteAction.CASCADE)
    @OneToMany(mappedBy = "scheduleEntity", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<ReservationDetailsEntity> reservationDetailsEntity;

    @OnDelete(action= OnDeleteAction.CASCADE)
    @OneToOne(mappedBy = "scheduleEntity", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private StatisticsEntity statisticsEntity; //1:1 이쪽이 부모요소이므로 아이디만 받기

    /**
     * 스케줄 영화 변경 도메인 메서드
     * @param movieEntity 영화 FK
     */
    public void changeMovie(MovieEntity movieEntity) {
        this.movieEntity = movieEntity;
    }

    /**
     * 스케줄 좌석정책 변경 도메인 메서드
     * @param theaterEntity
     */
    public void changeTheater(TheaterEntity theaterEntity) {
        this.theaterEntity = theaterEntity;
    }

    /**
     * Entity -> DTO
     * @param scheduleEntity
     * @return DTO
     */
    public static ScheduleDTO toDTO(ScheduleEntity scheduleEntity) {

        return ScheduleDTO.builder()
                .id(scheduleEntity.getId())
                .theater(TheaterEntity.toDTO(scheduleEntity.getTheaterEntity()))
                .movie(MovieEntity.toDTO(scheduleEntity.getMovieEntity()))
                .startAt(scheduleEntity.getStartAt())
                .endAt(scheduleEntity.getEndAt())
                .build();
    }
}
