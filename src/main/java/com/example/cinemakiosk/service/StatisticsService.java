package com.example.cinemakiosk.service;

import com.example.cinemakiosk.dto.StatisticsDTO;

import java.time.LocalDate;
import java.util.List;

public interface StatisticsService {
    // 통계 테이블 기간별 통계 데이터 (년, 월, 일)
    List<StatisticsDTO> getStatistics(LocalDate startDate, LocalDate endDate, String type);
}
