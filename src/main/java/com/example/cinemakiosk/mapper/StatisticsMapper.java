package com.example.cinemakiosk.mapper;

import com.example.cinemakiosk.vo.StatisticsVO;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface StatisticsMapper {
    // 통계 테이블 기간별 통계 데이터 계산(년, 월, 일)
    List<StatisticsVO> getStatistics(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("type") String type);
}
