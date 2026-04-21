package com.example.cinemakiosk.vo;

import com.example.cinemakiosk.domain.enums.Days;
import com.example.cinemakiosk.dto.StatisticsDTO;
import lombok.*;

import java.time.LocalDate;


@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class StatisticsVO {

    private Long id;
    private ScheduleVO schedule;
    private Days day;          // 요일
    private Long revenue;        // 수익
    private Long customerCount;  // 관람객 수
    private LocalDate date;  // 날짜
    private Long scheduleId;

    /**
     * VO -> DTO
     * @param statisticsVO
     * @return
     */
    public static StatisticsDTO toDTO(StatisticsVO statisticsVO) {
        return StatisticsDTO.builder()
                .id(statisticsVO.getId())
                .schedule(ScheduleVO.toDTO(statisticsVO.getSchedule()))
                .day(statisticsVO.getDay())
                .revenue(statisticsVO.getRevenue())
                .customerCount(statisticsVO.getCustomerCount())
                .date(statisticsVO.getDate())
                .build();
    }
}