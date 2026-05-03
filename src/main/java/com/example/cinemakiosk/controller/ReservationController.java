package com.example.cinemakiosk.controller;

import com.example.cinemakiosk.dto.ReservationDetailsDTO;
import com.example.cinemakiosk.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reservation")
public class ReservationController {
    private final ReservationService reservationService;

    @Operation(summary = "스케줄에서 예약된 좌석 조회")
    @GetMapping("/seatCount/schedule/{scheduleId}")
    public ResponseEntity<List<String>> getUseSeatByScheduleId(@PathVariable Long scheduleId){
        List<String> seatList = reservationService.readAllReservationSeatByScheduleId(scheduleId);
        return ResponseEntity.ok(seatList);
    }

    @Operation(summary = "영화 아이디로 예약된 좌석 전체 조회")
    @GetMapping("/seatCount/movie/{movieId}")
    public ResponseEntity<List<ReservationDetailsDTO>> getUseSeatByMovieId(@PathVariable Long movieId){
        log.info("받은 movieId : {}",movieId);

        List<ReservationDetailsDTO> reservationDetailsDTOS = reservationService.readSeatByMovieId(movieId);

        return ResponseEntity.ok(reservationDetailsDTOS);
    }
}
