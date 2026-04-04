package com.example.cinemakiosk.service;

import com.example.cinemakiosk.dto.SeatPolicyDTO;
import com.example.cinemakiosk.dto.TheaterDTO;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@Log4j2
@SpringBootTest
class TheaterServiceImplTest {
    @Autowired private TheaterService theaterService;

    @Test
    void createTheater() {
        TheaterDTO theaterDTO = TheaterDTO.builder()
                .policyId(1L)
                .cleanupTime(1000L)
                .build();
        theaterService.createTheater(theaterDTO);
    }

    @Test
    void getTheaterAllTest() {
        theaterService.getTheaterAll().forEach(log::info);
    }

    @Test
    void getTheater() {
        log.info(theaterService.getTheater(1L));
    }

    @Test
    void updateSeatPolicy() {
        List<Long> theaterDTOList = List.of(1L, 2L);
        Long policyId = 3L;
        theaterService.updateSeatPolicy(theaterDTOList, policyId);
    }

    @Test
    void updateCleanTime() {
        List<Long> theaterDTOList = List.of(1L, 2L);
        Long cleanupTime = 1000L;
        theaterService.updateCleanTime(theaterDTOList, cleanupTime);
    }

    @Test
    void createSeat() {
        SeatPolicyDTO seatPolicyDTO = SeatPolicyDTO.builder()
                        .name("일반")
                        .cost(5000L)
                        .build();

        log.info("theaterDTO: {}", seatPolicyDTO);
        theaterService.createSeat(seatPolicyDTO);
    }

    @Test
    void readAllSeat() {
        theaterService.readAllSeat().forEach(log::info);
    }

    @Test
    void readSeat() {
        log.info("readSeat... 지정 좌석 정책 : {}", theaterService.readSeat(4L));
    }

    @Test
    void updateSeat() {
        SeatPolicyDTO policyDTO = SeatPolicyDTO.builder()
                .policyId(4L)
                .name("수정")
                .cost(1000L)
                .build();

        theaterService.updateSeat(policyDTO);
    }

    @Test
    void deleteSeat() {
        Long policyId = 5L;
        theaterService.deleteSeat(policyId);
    }
}