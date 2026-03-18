package com.example.cinemakiosk.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;
import java.util.List;

/**
 * PreviewController
 * ─────────────────────────────────────────────────────────────────────────
 * view 브랜치 전용 임시 컨트롤러.
 * 백엔드 작업 전 뷰 레이아웃 확인용으로, 실제 서비스 URL을 그대로 매핑한다.
 * <p>
 * 머지 시 실제 서비스 컨트롤러(MainController, BookingController 등)로 대체되므로
 * 이 파일 전체를 삭제하면 된다.
 * <p>
 * 각 핸들러에서 dummy Model 데이터를 주입해 Thymeleaf 렌더링 오류를 방지한다.
 * <p>
 * ▶ 접근 URL 목록
 * ──────────────────────────────────────────
 * [고객 영역]
 *   /movie/list          → UC-01 상영작 목록
 *   /movie/{id}          → UC-02 상영작 상세
 *   /booking/schedule    → UC-03 날짜·시간·인원 선택
 *   /booking/seat        → UC-03 좌석 선택
 *   /payment/payment     → UC-04~06 결제·포인트
 *   /payment/result      → UC-07 예매 완료 확인증
 *   /mypage/lookup       → UC-08 예매 조회
 *   /mypage/detail       → UC-08 예매 상세
 * [관리자 영역]
 *   /admin/login             → UC-11 관리자 로그인
 *   /admin/dashboard         → 관리자 대시보드
 *   /admin/stats/daily       → UC-12 일일 통계
 *   /admin/stats/monthly     → UC-13 월별 통계
 *   /admin/stats/by-day      → UC-14 요일별 통계
 *   /admin/stats/by-hour     → UC-15 시간대별 통계
 *   /admin/stats/by-movie    → UC-16 영화별 통계
 *   /admin/refund            → UC-17 환불 처리
 *   /admin/movie/form        → UC-18~19 영화 등록·수정
 *   /admin/movie/manage      → UC-20 영화 목록·삭제
 *   /admin/theater/edit      → UC-21 상영관 수정
 * ─────────────────────────────────────────────────────────────────────────
 */
@Controller
public class PreviewController {

    // ══════════════════════════════════════════════════════════════════
    // 고객 영역
    // ══════════════════════════════════════════════════════════════════

    /** UC-01 상영작 목록 */
    @GetMapping("/movie/list")
    public String movieList(Model model) {
        return "movie/list";
    }

    /**
     * UC-02 상영작 상세.
     * 실제 서비스에서는 movieId로 DB 조회 후 MovieDTO를 주입한다.
     * view 브랜치에서는 더미 데이터로 렌더링 확인용.
     *
     * @param id  경로 변수 (예: /movie/1)
     */
    @GetMapping("/movie/{id}")
    public String movieDetail(@PathVariable Long id, Model model) {

        /* 영화 기본 정보 더미 — detail.html 의 MovieDTO 필드와 대응 */
        model.addAttribute("movie", new java.util.HashMap<String, Object>() {{
            put("movieId",     id);
            put("title",       "[미리보기] 더미 영화 #" + id);
            put("genre",       "액션");
            put("rating",      "15");
            put("runtime",     120L);
            put("director",    "홍길동");
            put("actor",       "김철수, 이영희");
            put("description", "줄거리: view 브랜치 미리보기용 더미 데이터입니다. 실제 내용은 백엔드 연동 후 표시됩니다.");
            // #temporals.format() 이 LocalDateTime 타입을 요구하므로 반드시 LocalDateTime 객체로 전달
            put("startAt",     LocalDateTime.of(2026, 3, 1, 0, 0));
            put("endAt",       LocalDateTime.of(2026, 5, 31, 23, 59));
        }});

        /* 포스터 URL — placeholder 사용 */
        model.addAttribute("posterUrl",  "/images/placeholder-poster.jpg");

        /* 매진 여부 — 기본 false (예매 버튼 활성화 확인용) */
        model.addAttribute("isSoldOut",  false);

        return "movie/detail";
    }

    /** UC-03 날짜·시간·인원 선택 */
    @GetMapping("/booking/schedule")
    public String bookingSchedule(Model model) {
        return "booking/schedule";
    }

    /** UC-03 좌석 선택 */
    @GetMapping("/booking/seat")
    public String bookingSeat(Model model) {
        return "booking/seat";
    }

    /**
     * UC-04~06 결제·포인트.
     * ※ 파일 위치: templates/payment/payment.html
     *    (booking/ 에서 payment/ 로 이동됨)
     */
    @GetMapping("/payment/payment")
    public String paymentPayment(Model model) {
        /*
         * Thymeleaf 렌더링 오류 방지용 최소 dummy 데이터.
         * 실제 BookingController에서는 DB 조회 결과를 주입.
         */

        /* 영화 정보 (movie.html의 MovieDTO 구조 대응) */
        model.addAttribute("movie", new java.util.HashMap<String, Object>() {{
            put("movieId", 1L);
            put("title",   "[미리보기] 더미 영화");
            put("rating",  "15");
        }});

        /* 상영 스케줄 (ScheduleDTO 구조 대응) */
        model.addAttribute("schedule", new java.util.HashMap<String, Object>() {{
            put("id",        1L);
            put("no",        1);
            put("movieId",   1L);
            put("startTime", "2026-03-20T14:00");
            put("endTime",   "2026-03-20T16:10");
        }});

        /* 상영관 (TheaterDTO 구조 대응) */
        model.addAttribute("theater", new java.util.HashMap<String, Object>() {{
            put("no",   1);
            put("name", "1관");
            put("cost", 15000);
        }});

        /* 예매 정보 (ReservationDetailsDTO 구조 대응) */
        model.addAttribute("reservation", new java.util.HashMap<String, Object>() {{
            put("id",          "DUMMY-RESERVATION-001");
            put("scheduleId",  1L);
            put("seatNumber",  List.of("C3", "C4"));
        }});

        /* 인원 */
        model.addAttribute("adultCount", 2);
        model.addAttribute("teenCount",  0);

        /* 할인 정책 목록 (빈 리스트 — 실제는 DiscountPolicyDTO 목록) */
        model.addAttribute("discountPolicies", List.of());

        /* 적립 정책 목록 (빈 리스트 — 실제는 BonusPolicyDTO 목록) */
        model.addAttribute("bonusPolicies", List.of());

        /* 인증된 회원 없음 (미인증 상태) */
        model.addAttribute("member", null);

        return "payment/payment";
    }

    /**
     * UC-07 예매 완료 확인증.
     * ※ 파일 위치: templates/payment/result.html
     *    (booking/confirm → payment/result 로 이름·위치 변경됨)
     */
    @GetMapping("/payment/result")
    public String paymentResult(Model model) {
        /* TODO: 결제 완료 확인증 dummy 데이터 — result.html 구현 후 채울 것 */
        return "payment/result";
    }

    /** UC-08 예매 조회 입력 */
    @GetMapping("/mypage/lookup")
    public String mypageLookup(Model model) {
        return "mypage/lookup";
    }

    /** UC-08 예매 상세 */
    @GetMapping("/mypage/detail")
    public String mypageDetail(Model model) {
        return "mypage/booking-detail";
    }

    // ══════════════════════════════════════════════════════════════════
    // 관리자 영역
    // ══════════════════════════════════════════════════════════════════

    /** UC-11 관리자 로그인 */
    @GetMapping("/admin/login")
    public String adminLogin(Model model) {
        return "admin/login";
    }

    /** 관리자 대시보드 */
    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model) {
        return "admin/dashboard";
    }

    /** UC-12 일일 통계 */
    @GetMapping("/admin/stats/daily")
    public String statsDaily(Model model) {
        return "admin/stats/daily";
    }

    /** UC-13 월별 통계 */
    @GetMapping("/admin/stats/monthly")
    public String statsMonthly(Model model) {
        return "admin/stats/monthly";
    }

    /** UC-14 요일별 통계 */
    @GetMapping("/admin/stats/by-day")
    public String statsByDay(Model model) {
        return "admin/stats/by-day";
    }

    /** UC-15 시간대별 통계 */
    @GetMapping("/admin/stats/by-hour")
    public String statsByHour(Model model) {
        return "admin/stats/by-hour";
    }

    /** UC-16 영화별 통계 */
    @GetMapping("/admin/stats/by-movie")
    public String statsByMovie(Model model) {
        return "admin/stats/by-movie";
    }

    /**
     * UC-17 환불 처리.
     * ※ 파일 위치: templates/admin/management/theater/refund.html
     */
    @GetMapping("/admin/refund")
    public String adminRefund(Model model) {
        return "admin/management/theater/refund";
    }

    /**
     * UC-18~19 영화 등록·수정 폼.
     * ※ 파일 위치: templates/admin/management/movie/form.html
     */
    @GetMapping("/admin/movie/form")
    public String adminMovieForm(Model model) {
        return "admin/management/movie/form";
    }

    /**
     * UC-20 영화 목록·상영 중지.
     * ※ 파일 위치: templates/admin/management/movie/manage.html
     */
    @GetMapping("/admin/movie/manage")
    public String adminMovieManage(Model model) {
        return "admin/management/movie/manage";
    }

    /**
     * UC-21 상영관 정보 수정.
     * ※ 파일 위치: templates/admin/management/theater/edit.html
     */
    @GetMapping("/admin/theater/edit")
    public String adminTheaterEdit(Model model) {
        return "admin/management/theater/edit";
    }
}
