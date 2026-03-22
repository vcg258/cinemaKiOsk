package com.example.cinemakiosk.mapper;

import com.example.cinemakiosk.dto.StatisticsDTO;
import com.example.cinemakiosk.domain.StatisticsEntity;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;


@Mapper
public interface StatisticsMapper {

    // 전체 통계 조회
    @Select("SELECT * FROM statistics")
    List<StatisticsEntity> findAll();

    // ID로 조회
    @Select("SELECT * FROM statistics WHERE id = #{id}")
    StatisticsEntity findById(@Param("id") Long id);

    // 스케쥴 ID로 조회 (영화별 통계)
    @Select("SELECT * FROM statistics WHERE schedule_id = #{scheduleId}")
    List<StatisticsEntity> findByScheduleId(@Param("scheduleId") Long scheduleId);

    // 요일별 조회
    @Select("SELECT * FROM statistics WHERE day = #{day}")
    List<StatisticsEntity> findByDay(@Param("day") String day);

    // 일일 통계 (특정 날짜)
    @Select("SELECT * FROM statistics WHERE DATE(date) = DATE(#{date})")
    List<StatisticsEntity> findByDate(@Param("date") LocalDateTime date);

    // 월간 통계 (특정 연월)
    @Select("SELECT * FROM statistics WHERE YEAR(date) = #{year} AND MONTH(date) = #{month}")
    List<StatisticsEntity> findByMonth(@Param("year") int year, @Param("month") int month);

    // 시간대별 통계
    @Select("SELECT * FROM statistics WHERE HOUR(date) = #{hour}")
    List<StatisticsEntity> findByHour(@Param("hour") int hour);

    // 요일별 수익 합계 집계
    @Select("""
            SELECT day,
                   SUM(revenue) AS totalRevenue,
                   SUM(customer_count) AS totalCustomerCount
            FROM statistics
            GROUP BY day
            ORDER BY FIELD(day, 'MON','TUE','WED','THU','FRI','SAT','SUN')
            """)
    @Results({
            @Result(property = "day", column = "day"),
            @Result(property = "totalRevenue", column = "totalRevenue"),
            @Result(property = "totalCustomerCount", column = "totalCustomerCount")
    })
    List<StatisticsDTO.DailySummary> getDailySummary();

    // 통계 등록
    @Insert("""
            INSERT INTO statistics (schedule_id, day, revenue, customer_count, date)
            VALUES (#{scheduleId}, #{day}, #{revenue}, #{customerCount}, #{date})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(StatisticsEntity statistics);
    // 통계는 한번 기록되면 수정/삭제 하지 않음
}