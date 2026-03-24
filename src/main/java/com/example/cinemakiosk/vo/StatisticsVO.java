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

    private final Long statisticsId;
    private final String day;          // 요일
    private final Long revenue;        // 수익
    private final Long customerCount;  // 관람객 수
    private final LocalDateTime date;  // 날짜

    // Entity → VO 변환
    public static StatisticsVO from(StatisticsEntity entity) {
        return StatisticsVO.builder()
                .statisticsId(entity.getStatisticsId())
                .day(entity.getDay() != null ? entity.getDay().name() : null)
                .revenue(entity.getRevenue())
                .customerCount(entity.getCustomerCount())
                .build();
    }
}