package com.example.cinemakiosk.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "statistics")
public class StatisticsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long statisticsId;             // 통계 고유번호 (PK)

    @Column(name = "schedule_id", nullable = false)
    private Long scheduleId;     // 스케쥴 아이디

    @Column(name = "day", nullable = false)
    @Enumerated(EnumType.STRING)
    private Day day;             // 요일 ENUM

    @Column(name = "revenue")
    private Long revenue;        // 수익

    @Column(name = "customer_count")
    private Long customerCount;  // 관람객 수

    @Column(name = "date", nullable = false)
    private LocalDateTime date;          // 통계 기준 일시 (일일/월간/시간대별 통계용)

    public enum Day {
        SUN, MON, TUE, WED, THU, FRI, SAT
    }
}

