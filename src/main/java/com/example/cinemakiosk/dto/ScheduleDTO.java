package com.example.cinemakiosk.dto;

import com.example.cinemakiosk.domain.ReservationDetailsEntity;
import com.example.cinemakiosk.domain.ScheduleEntity;
import com.example.cinemakiosk.domain.StatisticsEntity;
import com.example.cinemakiosk.vo.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleDTO {
    private Long id; // 스케줄 인덱스
    private TheaterDTO theater; // 상영관 정보 FK
    private MovieDTO movie; // 영화 번호 FK
    private LocalDateTime startAt; // 상영 시작 시간
    private LocalDateTime endAt; // 상영 종료 시간

    private List<ReservationDetailsDTO> reservationDetails; // 1:다
    private StatisticsDTO statistics; //1:1 이쪽이 부모요소이므로 아이디만 받기

    /**
     * DTO -> Entity
     * @param scheduleDTO
     * @return Entity
     */
    public static ScheduleEntity toEntity(ScheduleDTO scheduleDTO) {
        List<ReservationDetailsDTO> reservationDetailsDTOs = scheduleDTO.getReservationDetails();
        List<ReservationDetailsEntity> reservationDetailsEntitys = new ArrayList<>();

        for (ReservationDetailsDTO reservationDetailsDTO : reservationDetailsDTOs) {
            ReservationDetailsEntity reservationDetailsEntity = ReservationDetailsEntity.builder()
                    .id(reservationDetailsDTO.getId())
                    .build();

            reservationDetailsEntitys.add(reservationDetailsEntity);
        }

        StatisticsEntity statisticsEntity = StatisticsEntity.builder()
                .statisticsId(scheduleDTO.getStatistics().getId())
                .build();

        return ScheduleEntity.builder()
                .id(scheduleDTO.getId())
                .theaterEntity(TheaterDTO.toEntity(scheduleDTO.getTheater()))
                .movieEntity(MovieDTO.toEntity(scheduleDTO.getMovie()))
                .startAt(scheduleDTO.getStartAt())
                .endAt(scheduleDTO.getEndAt())
                .reservationDetailsEntity(reservationDetailsEntitys)
                .statisticsEntity(statisticsEntity)
                .build();
    }

    /**
     * DTO -> VO
     * @param scheduleDTO
     * @return VO
     */
    public static ScheduleVO toVO(ScheduleDTO scheduleDTO) {
        List<ReservationDetailsDTO> reservationDetailsDTOs = scheduleDTO.getReservationDetails();
        List<ReservationDetailsVO> reservationDetailsVOs = new ArrayList<>();

        for (ReservationDetailsDTO reservationDetailsDTO : reservationDetailsDTOs) {
            ReservationDetailsVO reservationDetailsVO = ReservationDetailsVO.builder()
                    .id(reservationDetailsDTO.getId())
                    .build();

            reservationDetailsVOs.add(reservationDetailsVO);
        }

        StatisticsVO statisticsVO = StatisticsVO.builder()
                .id(scheduleDTO.getStatistics().getId())
                .build();
        
        return ScheduleVO.builder()
                .id(scheduleDTO.getId())
                .theater(TheaterDTO.toVO(scheduleDTO.getTheater()))
                .movie(MovieDTO.toVO(scheduleDTO.getMovie()))
                .startAt(scheduleDTO.getStartAt())
                .endAt(scheduleDTO.getEndAt())
                .reservationDetails(reservationDetailsVOs)
                .statistics(statisticsVO)
                .build();
    }
}
