package com.example.cinemakiosk.vo;

import com.example.cinemakiosk.domain.enums.Days;
import com.example.cinemakiosk.dto.StatisticsDTO;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;


@Getter
@Builder
@ToString
@EqualsAndHashCode
public class StatisticsVO {

    private final Long id;
    private final ScheduleVO schedule;
    private final Days day;          // 요일
    private final Long revenue;        // 수익
    private final Long customerCount;  // 관람객 수
    private final LocalDate date;  // 날짜

    /**
     * VO -> DTO
     * @param statisticsVO
     * @return
     */
    public static StatisticsDTO toDTO(StatisticsVO statisticsVO){
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