package com.example.cinemakiosk.service;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@Log4j2
@SpringBootTest
class StatisticsServiceImplTest {
    @Autowired StatisticsService statisticsService;

    @Test
    void getStatistics() {
        LocalDate startDate = LocalDate.of(2020, 1, 1);
        LocalDate endDate = LocalDate.of(2026, 12, 31);
        String type = "MOVIE";
        log.info(statisticsService.getStatistics(startDate, endDate, type));
    }
}