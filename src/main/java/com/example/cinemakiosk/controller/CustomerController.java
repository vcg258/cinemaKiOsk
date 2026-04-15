package com.example.cinemakiosk.controller;

import com.example.cinemakiosk.dto.PointHistoryDTO;
import com.example.cinemakiosk.dto.ScheduleDTO;
import com.example.cinemakiosk.dto.SeatPolicyDTO;
import com.example.cinemakiosk.dto.TheaterDTO;
import com.example.cinemakiosk.service.DiscountPolicyService;
import com.example.cinemakiosk.service.MemberService;
import com.example.cinemakiosk.service.ScheduleService;
import com.example.cinemakiosk.service.TheaterService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
public class CustomerController {
    private final ScheduleService scheduleService;
    private final TheaterService theaterService;
    private final DiscountPolicyService discountPolicyService;
    private final MemberService memberService;

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

    @Operation(summary = "회원 포인트 사용 적립")
    @PostMapping("/member/point")
    public ResponseEntity<Void> pointHistoryUse(@RequestBody PointHistoryDTO pointHistoryDTO) {
        memberService.pointHistoryCreate(pointHistoryDTO);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "쿠폰 검증")
    @PostMapping("/coupon/auth")
    public ResponseEntity<Boolean> authCoupon(@RequestParam String couponNum) {
        return ResponseEntity.ok(discountPolicyService.authCoupon(couponNum));
    }
}
