package com.example.cinemakiosk.dto;

import com.example.cinemakiosk.domain.StatisticsEntity;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class StatisticsDTO {


        private Long scheduleId;
        private String day;
        private Long revenue;
        private Long customerCount;
        private LocalDateTime date;     // 통계 기준 일시

        public StatisticsEntity toEntity() {
            return StatisticsEntity.builder()
                    .scheduleId(this.scheduleId)
                    .day(StatisticsEntity.Day.valueOf(this.day))
                    .revenue(this.revenue)
                    .customerCount(this.customerCount)
                    .date(this.date)
                    .build();
        }

    // 응답 DTO
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @ToString
    public static class Response {
        private Long id;
        private Long scheduleId;
        private String day;
        private Long revenue;
        private Long customerCount;
        private LocalDateTime date;

        public static Response from(com.example.cinemakiosk.domain.StatisticsEntity entity) {
            return Response.builder()
                    .id(entity.getId())
                    .scheduleId(entity.getScheduleId())
                    .day(entity.getDay() != null ? entity.getDay().name() : null)
                    .revenue(entity.getRevenue())
                    .customerCount(entity.getCustomerCount())
                    .date(entity.getDate())
                    .build();
        }
    }

    // 요일별 집계 응답 DTO
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DailySummary {
        private String day;
        private Long totalRevenue;
        private Long totalCustomerCount;
    }
}
