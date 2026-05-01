package com.example.cinemakiosk.service;

import com.example.cinemakiosk.dto.requestDTO.ActivationRequest;
import com.example.cinemakiosk.dto.ScheduleDTO;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

@Log4j2
@SpringBootTest
class ScheduleServiceImplTest {
    @Autowired private ScheduleService scheduleService;

    @Test
    void createSchedule() {
        ScheduleDTO scheduleDTO = ScheduleDTO.builder()
                .startAt(LocalDateTime.now().minusHours(3))
                .movieId(1L)
                .no(3L)
                .build();
        scheduleService.createSchedule(scheduleDTO);
    }

    @Test
    void updateSchedule() {
        ScheduleDTO scheduleDTO = ScheduleDTO.builder()
                .id(11L)
                .startAt(LocalDateTime.now().minusHours(3))
                .build();
        scheduleService.updateSchedule(scheduleDTO);
    }

    @Test
    void updateActivation() {
        ActivationRequest request = new ActivationRequest();
        request.setIds(List.of(1L));
        request.setActivation(false);
        scheduleService.updateActivation(request);
    }

    @Test
    void deleteSchedule() {
        List<Long> ids = List.of(1L);
        scheduleService.deleteSchedule(ids);
    }

    @Test
    void getScheduleList() {
        scheduleService.getScheduleList().forEach(log::info);
    }

    @Test
    void getScheduleListByMovie() {
        scheduleService.getScheduleListByMovie(1L).forEach(log::info);
    }

    @Test
    void getScheduleListByMovieWithCustomer() {
        scheduleService.getScheduleListByMovieWithCustomer(1L).forEach(log::info);
    }

    @Test
    void getSchedule() {
        log.info(scheduleService.getSchedule(6L));
    }
}