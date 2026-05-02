package com.example.cinemakiosk.controller;

import com.example.cinemakiosk.domain.enums.Grade;
import org.springframework.http.HttpStatus;
import com.example.cinemakiosk.dto.*;
import com.example.cinemakiosk.service.*;

import com.example.cinemakiosk.dto.MovieDTO;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j2
@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
public class CustomerController {
    private final ScheduleService scheduleService;
    private final TheaterService theaterService;
    private final DiscountPolicyService discountPolicyService;
    private final MemberService memberService;
    private final MovieService movieService;
    private final BonusPolicyService bonusPolicyService;

    @Operation(summary = "스케줄 전체 조회")
    @GetMapping("/schedule/list")
    public ResponseEntity<List<ScheduleDTO>> getScheduleList() {
        return ResponseEntity.ok(scheduleService.getScheduleList());
    }

    @Operation(summary = "스케줄 객체 전체 조회")
    @GetMapping("/schedule/DTOlist")
    public ResponseEntity<List<ScheduleDTO>> getScheduleDTOList() {
        return ResponseEntity.ok(scheduleService.getScheduleDTOList());
    }

    @Operation(summary = "지정 영화에 해당하는 전체 스케줄 조회")
    @GetMapping("/schedule/{id}/movie")
    public ResponseEntity<List<ScheduleDTO>> getScheduleByMovie(@PathVariable Long id) {
        return ResponseEntity.ok(scheduleService.getScheduleListByMovieWithCustomer(id));
    }

    // 상영중인 영화 조회
    @Operation(summary = "오늘날짜에 스케쥴이 있는 영화 조회 (고객용)",
            description = "- 스케쥴을 조회해 영화정보를 불러오므로 영화정보의 start_at과 end_at은 노상관")
    @GetMapping("/movie/all")
    public ResponseEntity<List<MovieDTO>> readAll() {
        log.info("screening_period get...");
        List<MovieDTO> movieDTOList = movieService.getScreeningPeriodAllMovies();
        log.info("movieDTOList: {}", movieDTOList);
        return ResponseEntity.ok(movieDTOList);
    }

    // 단일 영화 조회 (고객 상세 페이지용)
    @Operation(summary = "단일 영화 조회", description = "movieId로 단일 영화 정보 조회")
    @GetMapping("/movie/{movieId}/readOne")
    public ResponseEntity<MovieDTO> getMovieById(@PathVariable Long movieId) {
        log.info("getMovieById get... id={}", movieId);
        return ResponseEntity.ok(movieService.getMovieById(movieId));
    }

    @PostMapping("/member/{phone}")
    public ResponseEntity<MemberDTO> postMemberById(@PathVariable String phone) {
        memberService.createMember(new MemberDTO(phone, Grade.NORMAL, 0, null));
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
    public ResponseEntity<CouponDTO> authCoupon(@RequestParam String couponNum) {
        return ResponseEntity.ok(discountPolicyService.authCoupon(couponNum));
    }

    @Operation(summary = "맴버 단일 조회")
    @GetMapping("/member/{phone}")
    public ResponseEntity<MemberDTO> getMemberById(@PathVariable String phone) {
        return ResponseEntity.ok(memberService.getMember(phone));
    }

    @Operation(summary = "상영관 내용 객체타입 변수 확인")
    @GetMapping("/theater/dtoAll")
    public ResponseEntity<List<TheaterDTO>> getDTOAllTheater() {
        return ResponseEntity.ok(theaterService.getTheaterDTOAll());
    }

    @Operation(summary = "상영관 전체 조회")
    @GetMapping("/theater/list")
    public ResponseEntity<List<TheaterDTO>> getAllTheater() {
        return ResponseEntity.ok(theaterService.getTheaterAll());
    }

    @Operation(summary = "좌석정책 전체 조회")
    @GetMapping("/seat-policy/list")
    public ResponseEntity<List<SeatPolicyDTO>> getAllSeatPolicies() {
        return ResponseEntity.ok(theaterService.readAllSeat());
    }

    @Operation(summary = "적림정책 전체 조회")
    @GetMapping("/bonus-policy/list")
    public ResponseEntity<List<BonusPolicyDTO>> getBonusPolicies() {
        return ResponseEntity.ok(bonusPolicyService.getBonusPolicies());
    }

    @Operation(summary = "연령 할인 정책 조회 (고객용)",
            description = "conditionType = AGE 인 활성 정책만 반환. 인원 선택 화면의 청소년/경로 할인 표시에 사용")
    @GetMapping("/discount/age")
    public ResponseEntity<List<DiscountPolicyDTO>> getAgeDiscounts() {
        return ResponseEntity.ok()
                .body(discountPolicyService.getAgeDiscounts());
    }

    @Operation(summary = "시간 할인 정책 조회 (고객용)",
            description = "conditionType = TIME 인 활성 정책만 반환. 조조 할인 여부 판정에 사용")
    @GetMapping("/discount/time")
    public ResponseEntity<List<DiscountPolicyDTO>> getTimeDiscounts() {
        return ResponseEntity.ok()
                .body(discountPolicyService.getTimeDiscounts());
    }

}
