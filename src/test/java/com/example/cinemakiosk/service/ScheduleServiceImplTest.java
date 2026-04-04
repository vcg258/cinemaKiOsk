package com.example.cinemakiosk.service;

import com.example.cinemakiosk.dto.RequestDTO.ActivationRequest;
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
    void getSchedule() {
        log.info(scheduleService.getSchedule(6L));
    }


    // TODO 클로드의 테스트 코드
    // =====================================================
    // createSchedule 테스트
    // =====================================================

    @Test
    void createSchedule_성공() {
        // 상영관 3번 (cleanup 20분), 영화 1번 (runtime 135분)
        // 더미에 상영관 3번 스케줄: 03-29 10:00~12:00, 03-30 10:00~11:35 → 겹치지 않는 시간
        ScheduleDTO dto = ScheduleDTO.builder()
                .no(3L)
                .movieId(1L)
                .startAt(LocalDateTime.of(2027, 1, 1, 9, 0))
                .activation(false)
                .build();
        scheduleService.createSchedule(dto);
    }

    @Test
    void createSchedule_시간겹침_등록실패() {
        // 상영관 1번 더미 스케줄: 03-29 09:00~11:15, 13:00~15:15
        // 09:30 시작 → 기존 09:00~11:15 와 겹침
        ScheduleDTO dto = ScheduleDTO.builder()
                .no(1L)
                .movieId(1L)
                .startAt(LocalDateTime.of(2026, 3, 29, 9, 30))
                .activation(false)
                .build();
        scheduleService.createSchedule(dto);
    }

    // =====================================================
    // updateSchedule 테스트
    // =====================================================

    @Test
    void updateSchedule_성공() {
        // 스케줄 ID 8번 (상영관 3번, 영화 5번, 03-30 10:00~11:35)
        // 미래 시간으로 변경, 상영관 3번의 다른 스케줄과 겹치지 않는 시간
        ScheduleDTO dto = ScheduleDTO.builder()
                .id(8L)
                .no(3L)
                .movieId(3L)
                .startAt(LocalDateTime.of(2027, 1, 1, 14, 0))
                .activation(false)
                .build();
        scheduleService.updateSchedule(dto);
    }

    @Test
    void updateSchedule_이미지난스케줄_수정실패() {
        // startAt을 과거 시간으로 지정 → 수정 불가
        ScheduleDTO dto = ScheduleDTO.builder()
                .id(7L)
                .no(2L)
                .movieId(3L)
                .startAt(LocalDateTime.of(2026, 1, 1, 9, 0))
                .activation(false)
                .build();
        scheduleService.updateSchedule(dto);
    }

    @Test
    void updateSchedule_시간겹침_수정실패() {
        // 스케줄 ID 7번 (상영관 2번)을 수정하는데
        // 상영관 2번의 다른 스케줄 ID 2번 (03-29 09:30~11:20) 과 겹치는 시간으로 수정 시도
        ScheduleDTO dto = ScheduleDTO.builder()
                .id(7L)
                .no(2L)
                .movieId(3L)
                .startAt(LocalDateTime.of(2026, 3, 29, 9, 30))
                .activation(false)
                .build();
        scheduleService.updateSchedule(dto);
    }

    // =====================================================
    // deleteSchedule 테스트
    // =====================================================

    @Test
    void deleteSchedule_성공() {
        // 스케줄 ID 8번 삭제
        List<Long> ids = List.of(1L);
        scheduleService.deleteSchedule(ids);
        // 삭제 후 조회 시 예외 발생 확인
        org.junit.jupiter.api.Assertions.assertThrows(Exception.class,
                () -> scheduleService.getSchedule(8L));
    }
}