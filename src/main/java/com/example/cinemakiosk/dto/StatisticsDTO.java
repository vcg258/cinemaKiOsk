package com.example.cinemakiosk.dto;

import com.example.cinemakiosk.domain.StatisticsEntity;
import com.example.cinemakiosk.domain.enums.Days;
import com.example.cinemakiosk.vo.StatisticsVO;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class StatisticsDTO {

    private Long id;
    private Long scheduleId;
    private Days day;
    private Long revenue;
    private Long customerCount;
    private LocalDate date;     // 통계 기준 일시

    /**
     * DTO -> Entity
     * @param statisticsDTO
     * @return Entity
     */
    public static StatisticsEntity toEntity(StatisticsDTO statisticsDTO){
        return StatisticsEntity.builder()
                .statisticsId(statisticsDTO.getId())
                .scheduleId(statisticsDTO.getScheduleId())
                .day(statisticsDTO.getDay())
                .revenue(statisticsDTO.getRevenue())
                .customerCount(statisticsDTO.getCustomerCount())
                .date(statisticsDTO.getDate())
                .build();
    }

    /**
     * DTO -> VO
     * @param statisticsDTO
     * @return VO
     */
    public static StatisticsVO toVO(StatisticsDTO statisticsDTO){
        return StatisticsVO.builder()
                .id(statisticsDTO.getId())
                .scheduleId(statisticsDTO.getScheduleId())
                .day(statisticsDTO.getDay())
                .revenue(statisticsDTO.getRevenue())
                .customerCount(statisticsDTO.getCustomerCount())
                .date(statisticsDTO.getDate())
                .build();
    }
}
