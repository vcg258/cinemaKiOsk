package com.example.cinemakiosk.service;

import com.example.cinemakiosk.dto.*;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Log4j2
@SpringBootTest
class ReservationServiceImplTest {

    @Autowired ReservationService reservationService;

    @Test
    public void addTest(){
        SeatPolicyDTO seatPolicyDTO = SeatPolicyDTO.builder()
                .policyId(1L)
//                .name("일반석")
//                .cost(5000L)
                .build();
//
        TheaterDTO theaterDTO = TheaterDTO.builder()
                .no(1L)
                .seatPolicy(seatPolicyDTO)
//                .cleanupTime(20L)
                .build();

        MovieDTO movieDTO = MovieDTO.builder()
                .movieId(1L)
//                .title("dm")
//                .genre("sdf")
//                .rating("ALL")
//                .runtime(200L)
//                .director("rkaehr")
//                .actors("qodnemf")
//                .description("tjfaud")
//                .startAt(LocalDateTime.now().plusMinutes(300))
//                .endAt(LocalDateTime.now().plusMinutes(500))
//                .createAt(LocalDateTime.now())
                .build();

        ScheduleDTO scheduleDTO = ScheduleDTO.builder()
                .id(1L)
                .theater(theaterDTO)
                .movie(movieDTO)
//                .startAt(LocalDateTime.now().plusMinutes(700))
//                .endAt(LocalDateTime.now().plusMinutes(900))
                .build();

        MemberDTO memberDTO = MemberDTO.builder()
                .phone("01088771113")
//                .point(0)
                .build();

        List<ReservationSeatDTO> list = new ArrayList<>();
        for (int i = 1; i < 5; i++) {
            ReservationSeatDTO reservationSeatDTO = ReservationSeatDTO.builder()
//                    .id(1L)
//                    .reservationDetailsId(1L)
                    .seatNumber("a"+i)
                    .build();

            list.add(reservationSeatDTO);
        }


        ReservationDetailsDTO reservationDetailsDTO = ReservationDetailsDTO.builder()
                .schedule(scheduleDTO)
                .phone(memberDTO)
                .reservationTime(LocalDateTime.now())
                .seats(list)
                .build();

        reservationService.create(reservationDetailsDTO);
    }

    @Test
    public void selectOne(){
        ReservationDetailsDTO read = reservationService.read(3L);
        log.info(read);
    }
}