package com.example.cinemakiosk.controller;

import com.example.cinemakiosk.dto.RequestDTO.TheaterRequest;
import com.example.cinemakiosk.dto.SeatPolicyDTO;
import com.example.cinemakiosk.dto.TheaterDTO;
import com.example.cinemakiosk.service.TheaterService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class TheaterController {
    private final TheaterService theaterService;

    @Operation(summary = "상영관 등록")
    @PostMapping("/theater")
    public ResponseEntity<Void> addTheater(@RequestBody TheaterDTO theaterDTO) {
        theaterService.createTheater(theaterDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "상영관 전체 조회")
    @GetMapping("/theater/list")
    public ResponseEntity<List<TheaterDTO>> getAllTheater() {
        return ResponseEntity.ok(theaterService.getTheaterAll());
    }

    @Operation(summary = "사영관 단일 조회")
    @GetMapping("/theater/{no}")
    public ResponseEntity<TheaterDTO> getTheater(@PathVariable Long no) {
        return ResponseEntity.ok(theaterService.getTheater(no));
    }

    @Operation(summary = "상영관 좌석 정책 수정")
    @PatchMapping("/theater/policy")
    public ResponseEntity<Void> modifyTheaterSeatPolicy(@RequestBody TheaterRequest request){
        theaterService.updateSeatPolicy(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "상영관 정리시간 수정")
    @PatchMapping("/theater/cleantime")
    public ResponseEntity<Void> modifyTheaterCleanTime(@RequestBody TheaterRequest request){
        theaterService.updateCleanTime( request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "좌석정책 등록")
    @PostMapping("/seat-policy")
    public ResponseEntity<Void> addSeatPolicy(@RequestBody SeatPolicyDTO seatPolicyDTO) {
        theaterService.createSeat(seatPolicyDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "좌석정책 전체 조회")
    @GetMapping("/seat-policy/list")
    public ResponseEntity<List<SeatPolicyDTO>> getAllSeatPolicies() {
        return ResponseEntity.ok(theaterService.readAllSeat());
    }

    @Operation(summary = "좌석정책 단일 조회")
    @GetMapping("/seat-policy/{no}")
    public ResponseEntity<SeatPolicyDTO> getSeatPolicy(@PathVariable Long no) {
        return ResponseEntity.ok(theaterService.readSeat(no));
    }

    @Operation(summary = "좌석정책 수정")
    @PatchMapping("/seat-policy")
    public ResponseEntity<Void> modifySeatPolicy(@RequestBody SeatPolicyDTO seatPolicyDTO) {
        theaterService.updateSeat(seatPolicyDTO);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "좌석정책 삭제")
    @DeleteMapping("/seat-policy/{no}")
    public ResponseEntity<Void> deleteSeatPolicy(@PathVariable Long no) {
        theaterService.deleteSeat(no);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "상영관 내용 객체타입 변수 확인")
    @GetMapping("/theater/dtoAll")
    public ResponseEntity<List<TheaterDTO>> getDTOAllTheater(){
        return ResponseEntity.ok( theaterService.getTheaterDTOAll());
    }
}