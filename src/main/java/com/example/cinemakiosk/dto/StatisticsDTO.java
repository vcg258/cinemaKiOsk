package com.example.cinemakiosk.dto;

import lombok.*;

import java.time.LocalDate;
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
    private LocalDate date;     // 통계 기준 일시

}
