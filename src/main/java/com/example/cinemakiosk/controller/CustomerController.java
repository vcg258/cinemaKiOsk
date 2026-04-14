package com.example.cinemakiosk.controller;

import com.example.cinemakiosk.dto.ScheduleDTO;
import com.example.cinemakiosk.dto.SeatPolicyDTO;
import com.example.cinemakiosk.dto.TheaterDTO;
import com.example.cinemakiosk.service.ScheduleService;
import com.example.cinemakiosk.service.TheaterService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
public class CustomerController {
    private final ScheduleService scheduleService;
    private final TheaterService theaterService;

    @Operation(summary = "스케줄 전체 조회")
    @GetMapping("/schedule/list")
    public ResponseEntity<List<ScheduleDTO>> getScheduleList(){
        return ResponseEntity.ok(scheduleService.getScheduleList());
    }

    @Operation(summary = "지정 영화에 해당하는 전체 스케줄 조회")
    @GetMapping("/schedule/{id}/movie")
    public ResponseEntity<List<ScheduleDTO>> getScheduleByMovie(@PathVariable Long id){
        return ResponseEntity.ok(scheduleService.getScheduleListByMovie(id));
    }

    @Operation(summary = "좌석정책 전체 조회")
    @GetMapping("/seat-policy/list")
    public ResponseEntity<List<SeatPolicyDTO>> getAllSeatPolicies() {
        return ResponseEntity.ok(theaterService.readAllSeat());
    }

    @Operation(summary = "상영관 전체 조회")
    @GetMapping("/theater/list")
    public ResponseEntity<List<TheaterDTO>> getAllTheater() {
        return ResponseEntity.ok(theaterService.getTheaterAll());
    }
}
