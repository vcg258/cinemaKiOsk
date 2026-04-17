package com.example.cinemakiosk.controller;

import com.example.cinemakiosk.dto.*;
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

    @Operation(summary = "스케줄 객체 전체 조회")
    @GetMapping("/schedule/DTOlist")
    public ResponseEntity<List<ScheduleDTO>> getScheduleDTOList(){
        return ResponseEntity.ok(scheduleService.getScheduleDTOList());
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

    @Operation(summary = "상영관 내용 객체타입 변수 확인")
    @GetMapping("/theater/dtoAll")
    public ResponseEntity<List<TheaterDTO>> getDTOAllTheater(){
        return ResponseEntity.ok( theaterService.getTheaterDTOAll());
    }

    @Operation(summary = "맴버 단일 조회")
    @GetMapping("/member/{phone}")
    public ResponseEntity<MemberDTO> getMemberById(@PathVariable String phone){
        return ResponseEntity.ok(memberService.getMember(phone));
    }

    @PostMapping("/member/{phone}")
    public ResponseEntity<MemberDTO> postMemberById(@PathVariable String phone){
        memberService.createMember(new MemberDTO(phone,0,null));
        return ResponseEntity.ok(memberService.getMember(phone));
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
