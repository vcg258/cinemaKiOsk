package com.example.cinemakiosk.domain;

import com.example.cinemakiosk.dto.*;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@ToString (exclude = {"theaterEntity", "reservationDetailsEntity", "movieEntity"})
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

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean activation; // 활성화 여부 (유효 = True, 비활성화 = False)

    @OnDelete(action= OnDeleteAction.CASCADE)
    @OneToMany(mappedBy = "scheduleEntity", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<ReservationDetailsEntity> reservationDetailsEntity;

    /**
     * 스케줄 활성화 여부 변경
     * @param activation 스케줄 활성화 여부
     */
    public void changeActivation(boolean activation) {
        if (this.activation == activation) {
            return;
        }
        this.activation = activation;
    }

    /**
     * 스케줄 상영시간 변경 도메인 메서드
     * @param startAt 상영시작 시간
     * @param endAt 상영종료 시간
     */
    public void changeStartAt(LocalDateTime startAt, LocalDateTime endAt) {
        this.startAt = startAt;
        this.endAt = endAt;
    }

    /**
     * 스케줄 영화 변경 도메인 메서드
     * @param movieEntity 영화 FK
     */
    public void changeMovie(MovieEntity movieEntity) {
        this.movieEntity = movieEntity;
    }

    /**
     * 스케줄 좌석정책 변경 도메인 메서드
     * @param theaterEntity 좌석정책 FK
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
                .no(scheduleEntity.getTheaterEntity().getNo())
                .movieId(scheduleEntity.getMovieEntity().getMovieId())
                .startAt(scheduleEntity.getStartAt())
                .endAt(scheduleEntity.getEndAt())
                .activation(scheduleEntity.isActivation())
                .build();
    }
}
