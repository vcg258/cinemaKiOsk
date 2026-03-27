package com.example.cinemakiosk.service;

import com.example.cinemakiosk.dto.SeatPolicyDTO;
import com.example.cinemakiosk.dto.TheaterDTO;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import static org.junit.jupiter.api.Assertions.*;

@Log4j2
@SpringBootTest
class TheaterServiceImplTest {
    @Autowired private TheaterService theaterService;

    @Test
    void createSeat() {
        TheaterDTO theaterDTO = TheaterDTO.builder()
                .seatPolicyDTO(SeatPolicyDTO.builder()
                        .name("일반석")
                        .cost(7000L)
                        .build())
                    .build();

        log.info("theaterDTO: {}", theaterDTO);
        theaterService.createSeat(theaterDTO);
    }

    @Test
    void readSeat() {
    }

    @Test
    void readAllSeat() {
    }

    @Test
    void updateSeat() {
    }

    @Test
    void deleteSeat() {
    }
}