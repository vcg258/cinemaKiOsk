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


        private Long statisticsId;
        private String day;
        private Long revenue;
        private Long customerCount;
        private LocalDateTime date;     // 통계 기준 일시


}
