package com.example.cinemakiosk.domain;

import com.example.cinemakiosk.domain.MovieEntity.MovieEntity;
import com.example.cinemakiosk.dto.*;
import com.example.cinemakiosk.vo.ReservationDetailsVO;
import com.example.cinemakiosk.vo.ScheduleVO;
import com.example.cinemakiosk.vo.StatisticsVO;
import jakarta.persistence.*;
import lombok.*;

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

    @OneToMany(mappedBy = "scheduleEntity", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<ReservationDetailsEntity> reservationDetailsEntity;

    @OneToOne(mappedBy = "scheduleEntity", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private StatisticsEntity statisticsEntity; //1:1 이쪽이 부모요소이므로 아이디만 받기

    /**
     * Entity -> DTO
     * @param scheduleEntity
     * @return DTO
     */
    public static ScheduleDTO toDTO(ScheduleEntity scheduleEntity) {
        List<ReservationDetailsEntity> reservationDetailsEntitys = scheduleEntity.getReservationDetailsEntity();
        List<ReservationDetailsDTO> reservationDetailsDTOs = new ArrayList<>();

        for (ReservationDetailsEntity reservationDetailsEntity : reservationDetailsEntitys) {
            ReservationDetailsDTO reservationDetailsDTO = ReservationDetailsDTO.builder()
                    .id(reservationDetailsEntity.getId())
                    .build();

            reservationDetailsDTOs.add(reservationDetailsDTO);
        }

        StatisticsDTO statisticsDTO = StatisticsDTO.builder()
                .id(scheduleEntity.getStatisticsEntity().getStatisticsId())
                .build();

        return ScheduleDTO.builder()
                .id(scheduleEntity.getId())
                .theater(TheaterEntity.toDTO(scheduleEntity.getTheaterEntity()))
                .movie(MovieEntity.toDTO(scheduleEntity.getMovieEntity()))
                .startAt(scheduleEntity.getStartAt())
                .endAt(scheduleEntity.getEndAt())
                .reservationDetails(reservationDetailsDTOs)
                .statistics(statisticsDTO)
                .build();
    }
}
