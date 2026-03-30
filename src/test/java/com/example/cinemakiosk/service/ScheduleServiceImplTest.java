package com.example.cinemakiosk.service;

import com.example.cinemakiosk.domain.MovieEntity;
import com.example.cinemakiosk.dto.MovieDTO;
import com.example.cinemakiosk.dto.ScheduleDTO;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@Log4j2
@SpringBootTest
class ScheduleServiceImplTest {
    @Autowired private ScheduleService scheduleService;

    @Test
    void createSchedule() {

        // 영화관 테이블에서 런타임 가져옴

        ScheduleDTO scheduleDTO = ScheduleDTO.builder()
                .startAt(LocalDateTime.now())
                .endAt(LocalDateTime.now().plusHours(2))
                .movieId(1L)
                .no(1L)
                .build();
        scheduleService.createSchedule(scheduleDTO);
    }

    @Test
    void updateSchedule() {
    }

    @Test
    void deleteSchedule() {
    }

    @Test
    void getScheduleList() {
    }

    @Test
    void getScheduleListByMovie() {
    }

    @Test
    void getSchedule() {
    }
}