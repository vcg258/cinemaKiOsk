package com.example.cinemakiosk.repository;

import com.example.cinemakiosk.domain.StatisticsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface StatisticsRepository extends JpaRepository<StatisticsEntity, Long> {

    // 스케쥴 ID로 조회 (영화별 통계)
    List<StatisticsEntity> findByScheduleId(Long scheduleId);

    // 요일별 조회
    List<StatisticsEntity> findByDay(StatisticsEntity.Day day);

    // 일일 통계 (특정 날짜)
    @Query("SELECT s FROM StatisticsEntity s WHERE FUNCTION('DATE', s.date) = FUNCTION('DATE', :date)")
    List<StatisticsEntity> findByDate(@Param("date") LocalDateTime date);

    // 월간 통계 (기간으로 조회)
    List<StatisticsEntity> findByDateBetween(LocalDateTime start, LocalDateTime end);

    // 시간대별 통계
    @Query("SELECT s FROM StatisticsEntity s WHERE FUNCTION('HOUR', s.date) = :hour")
    List<StatisticsEntity> findByHour(@Param("hour") int hour);

    // 요일별 총 수익 집계
    @Query("SELECT s.day, SUM(s.revenue) FROM StatisticsEntity s GROUP BY s.day")
    List<Object[]> sumRevenueByDay();

    // 요일별 총 관람객 수 집계
    @Query("SELECT s.day, SUM(s.customerCount) FROM StatisticsEntity s GROUP BY s.day")
    List<Object[]> sumCustomerCountByDay();
}