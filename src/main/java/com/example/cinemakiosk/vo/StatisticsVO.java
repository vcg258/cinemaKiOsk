package com.example.cinemakiosk.vo;

import com.example.cinemakiosk.domain.StatisticsEntity;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;


@Getter
@Builder
@ToString
@EqualsAndHashCode
public class StatisticsVO {

    private final Long id;
    private final Long scheduleId;
    private final String day;          // 요일
    private final Long revenue;        // 수익
    private final Long customerCount;  // 관람객 수
    private final LocalDateTime date;

    // Entity → VO 변환
    public static StatisticsVO from(StatisticsEntity entity) {
        return StatisticsVO.builder()
                .id(entity.getId())
                .scheduleId(entity.getScheduleId())
                .day(entity.getDay() != null ? entity.getDay().name() : null)
                .revenue(entity.getRevenue())
                .customerCount(entity.getCustomerCount())
                .build();
    }
}