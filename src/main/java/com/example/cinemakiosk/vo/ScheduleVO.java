package com.example.cinemakiosk.vo;

import com.example.cinemakiosk.domain.ReservationDetailsEntity;
import com.example.cinemakiosk.domain.ScheduleEntity;
import com.example.cinemakiosk.domain.StatisticsEntity;
import com.example.cinemakiosk.dto.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleVO {
    private Long id; // 스케줄 인덱스
    private TheaterVO theater; // 상영관 정보
    private MovieVO movie; // 영화 번호 FK
    private LocalDateTime startAt; // 상영 시작 시간
    private LocalDateTime endAt; // 상영 종료 시간
    private List<ReservationDetailsVO> reservationDetails; // 1:다
    private StatisticsVO statistics; //1:1 이쪽이 부모요소이므로 아이디만 받기

    /**
     * VO -> DTO
     * @param scheduleVO
     * @return DTO
     */
    public static ScheduleDTO toDTO(ScheduleVO scheduleVO) {
        List<ReservationDetailsVO> reservationDetailsVOs = scheduleVO.getReservationDetails();
        List<ReservationDetailsDTO> reservationDetailsDTOs = new ArrayList<>();

        for (ReservationDetailsVO reservationDetailsVO : reservationDetailsVOs) {
            ReservationDetailsDTO reservationDetailsDTO = ReservationDetailsDTO.builder()
                    .id(reservationDetailsVO.getId())
                    .build();

            reservationDetailsDTOs.add(reservationDetailsDTO);
        }

        StatisticsDTO statisticsDTO = StatisticsDTO.builder()
                .id(scheduleVO.getStatistics().getId())
                .build();

        return ScheduleDTO.builder()
                .id(scheduleVO.getId())
                .theater(TheaterVO.toDTO(scheduleVO.getTheater()))
                .movie(MovieVO.toDTO(scheduleVO.getMovie()))
                .startAt(scheduleVO.getStartAt())
                .endAt(scheduleVO.getEndAt())
                .reservationDetails(reservationDetailsDTOs)
                .statistics(statisticsDTO)
                .build();
    }
}
