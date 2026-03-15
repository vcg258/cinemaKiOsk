package com.example.cinemakiosk.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * PreviewController
 * ─────────────────────────────────────────────────────────────────────────
 * 프론트엔드 레이아웃/컴포넌트 개발 중 미리보기용 임시 컨트롤러.
 * 실제 서비스 컨트롤러가 완성되면 해당 매핑이 대체되므로 삭제 불필요.
 * <p>
 * ▶ 접근 URL 목록 (모두 /preview prefix)
 * ──────────────────────────────────────────
 * [공통]
 *   /preview               → 공통 레이아웃 쇼케이스
 *   /preview/admin         → 관리자 레이아웃 미리보기
 * [고객 영역]
 *   /preview/movie/list       → UC-01 상영작 목록
 *   /preview/movie/detail     → UC-02 상영작 상세
 *   /preview/booking/schedule → UC-03 날짜·시간·인원 선택
 *   /preview/booking/seat     → UC-03 좌석 선택
 *   /preview/booking/payment  → UC-04~06 결제·포인트
 *   /preview/booking/confirm  → UC-07 예매 완료
 *   /preview/mypage/lookup    → UC-08 예매 조회
 *   /preview/mypage/detail    → UC-08 예매 상세
 *   /preview/auth/phone       → UC-05 휴대폰 인증
 * [관리자 영역]
 *   /preview/admin/login          → UC-11 관리자 로그인
 *   /preview/admin/dashboard      → 관리자 대시보드
 *   /preview/admin/stats/daily    → UC-12 일일 통계
 *   /preview/admin/stats/monthly  → UC-13 월별 통계
 *   /preview/admin/stats/by-day   → UC-14 요일별 통계
 *   /preview/admin/stats/by-hour  → UC-15 시간대별 통계
 *   /preview/admin/stats/by-movie → UC-16 영화별 통계
 *   /preview/admin/refund         → UC-17 환불 처리
 *   /preview/admin/movie/form     → UC-18~19 영화 등록·수정
 *   /preview/admin/movie/manage   → UC-20 영화 목록·삭제
 *   /preview/admin/theater/edit   → UC-21 상영관 수정
 * ─────────────────────────────────────────────────────────────────────────
 */
@Controller
@RequestMapping("/preview")
public class PreviewController {

    // ══════════════════════════════════════════════════════════════════
    // 공통 레이아웃 미리보기
    // ══════════════════════════════════════════════════════════════════

    /** 고객용 공통 레이아웃 미리보기 */
    @GetMapping
    public String previewLayout(Model model) {
        model.addAttribute("activeTab", "movies");
        return "preview/layout";
    }

    /** 관리자 레이아웃 미리보기 */
    @GetMapping("/admin")
    public String previewAdmin(Model model) {
        return "preview/admin";
    }

    // ══════════════════════════════════════════════════════════════════
    // 고객 영역
    // ══════════════════════════════════════════════════════════════════

    /** UC-01 상영작 목록 */
    @GetMapping("/movie/list")
    public String movieList(Model model) {
        return "movie/list";
    }

    /** UC-02 상영작 상세 */
    @GetMapping("/movie/detail")
    public String movieDetail(Model model) {
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

    /** UC-04~06 결제·포인트 */
    @GetMapping("/booking/payment")
    public String bookingPayment(Model model) {
        return "booking/payment";
    }

    /** UC-07 예매 완료 확인증 */
    @GetMapping("/booking/confirm")
    public String bookingConfirm(Model model) {
        return "booking/confirm";
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

    /** UC-05 휴대폰 인증 단독 페이지 */
    @GetMapping("/auth/phone")
    public String authPhone(Model model) {
        return "auth/phone-verify";
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

    /** UC-17 환불 처리 */
    @GetMapping("/admin/refund")
    public String adminRefund(Model model) {
        return "admin/refund";
    }

    /** UC-18~19 영화 등록·수정 폼 */
    @GetMapping("/admin/movie/form")
    public String adminMovieForm(Model model) {
        return "admin/movie/form";
    }

    /** UC-20 영화 목록·상영 중지 */
    @GetMapping("/admin/movie/manage")
    public String adminMovieManage(Model model) {
        return "admin/movie/manage";
    }

    /** UC-21 상영관 정보 수정 */
    @GetMapping("/admin/theater/edit")
    public String adminTheaterEdit(Model model) {
        return "admin/theater/edit";
    }
}
