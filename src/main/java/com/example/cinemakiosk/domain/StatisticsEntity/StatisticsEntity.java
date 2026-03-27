package com.example.cinemakiosk.domain.StatisticsEntity;

import com.example.cinemakiosk.domain.ScheduleEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;

@Entity
@Getter
@ToString(exclude = {"scheduleEntity"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "statistics")
public class StatisticsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long statisticsId;             // 통계 고유번호 (PK)

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false, foreignKey = @ForeignKey(name = "fk_statistics_schedule_id"))
    private ScheduleEntity scheduleEntity;     // 스케쥴 아이디 (FK)

    @Column(name = "day", nullable = false)
    @Enumerated(EnumType.STRING)
    private Day day;             // 요일 ENUM

    @Column(name = "revenue", nullable = false)
    private Long revenue;        // 수익

    @Column(name = "customer_count", nullable = false)
    private Long customerCount;  // 관람객 수

    @CreatedDate
    @Column(nullable = false, updatable = false) // TODO (, columnDefinition = "DATETIME DEFAULT NOW()") 유무 체크
    private LocalDate date;          // 통계 기준 일시 (일일/월간 통계용)
}

