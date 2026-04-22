package com.example.cinemakiosk.controller;

import com.example.cinemakiosk.dto.StatisticsDTO;
import com.example.cinemakiosk.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin/statistics")
@RequiredArgsConstructor
public class StatisticsController {
    private final StatisticsService statisticsService;

    @Operation(summary = "연, 월, 일, 시간, 영화별 통계", description = "type=YEAR, MONTH, DAY, HOUR, MOVIE")
    @GetMapping
    public ResponseEntity<List<StatisticsDTO>> getStatistics (LocalDate startDate, LocalDate endDate, String type) {
        return ResponseEntity.ok(statisticsService.getStatistics(startDate, endDate, type));
    }
}
