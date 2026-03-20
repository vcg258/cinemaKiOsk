package com.example.cinemakiosk.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * PreviewController — view 브랜치 전용 임시 컨트롤러
 * ─────────────────────────────────────────────────────────────────────────
 * 백엔드 작업 전 뷰 레이아웃 확인용. 실제 서비스 URL을 그대로 매핑하며,
 * Thymeleaf 렌더링 오류를 막기 위해 각 핸들러에서 더미 Model 데이터를 주입한다.
 *
 * ▶ 머지 시 이 파일 전체 삭제 후 실제 컨트롤러로 대체할 것.
 *
 * ▶ 접근 URL
 *   GET /movie/list, /movie/{id}
 *   GET /booking/schedule, /booking/seat
 *   GET /payment/payment, /payment/result
 *   GET /admin/login
 *   GET /admin/statistics/dashboard
 *   GET /admin/statistics/stats/daily~by-movie (5개)
 *   GET /admin/refund
 *   GET /admin/management/movie/form, /manage
 *   GET /admin/management/theater/edit
 *   GET /admin/management/seat/edit
 *   GET /admin/management/policy/list, /form, /manage
 * ─────────────────────────────────────────────────────────────────────────
 */
@Controller
public class PreviewController {

    // ══════════════════════════════════════════════════════════════════
    // 헬퍼 — 더미 데이터 팩토리
    // ══════════════════════════════════════════════════════════════════

    /**
     * MovieDTO 더미 생성.
     *
     * @param movieId 영화 ID
     * @param title   영화 제목
     * @param rating  관람 등급 (ALL / 12 / 15 / 19)
     */
    private Map<String, Object> dummyMovie(long movieId, String title, String rating) {
        Map<String, Object> m = new HashMap<>();
        m.put("movieId",     movieId);
        m.put("title",       title);
        m.put("genre",       "액션");
        m.put("rating",      rating);
        m.put("runtime",     120);
        m.put("director",    "홍길동");
        m.put("actor",       "김철수, 이영희");
        m.put("description", "[미리보기] 줄거리 더미 데이터입니다.");
        m.put("startAt",     "2026-03-01");
        m.put("endAt",       "2026-05-31");
        m.put("posterUrl",   "/images/placeholder-poster.jpg");
        return m;
    }

    /**
     * StatisticsDTO 더미 생성.
     *
     * @param scheduleId X축 식별자 (날짜 문자열, 시각 숫자, 요일 코드 등)
     * @param revenue    수익
     * @param count      관람객 수
     */
    private Map<String, Object> dummyStat(Object scheduleId, int revenue, int count) {
        Map<String, Object> s = new HashMap<>();
        s.put("id",            1);
        s.put("scheduleId",    scheduleId);
        s.put("day",           "MON");
        s.put("revenue",       revenue);
        s.put("customerCount", count);
        return s;
    }

    /**
     * TheaterDTO 더미 생성.
     *
     * @param no 상영관 번호
     */
    private Map<String, Object> dummyTheater(int no) {
        Map<String, Object> t = new HashMap<>();
        t.put("no",          no);
        t.put("name",        no + "관");
        t.put("cost",        15000);
        t.put("cleanupTime", 20);
        return t;
    }

    /**
     * SeatPolicyVO 더미 생성.
     *
     * @param policyId  정책 ID
     * @param name      정책명
     * @param cost      요금
     */
    private Map<String, Object> dummySeatPolicy(long policyId, String name, int cost) {
        Map<String, Object> p = new HashMap<>();
        p.put("policyId",   policyId);
        p.put("name",       name);
        p.put("cost",       cost);
        p.put("isRecliner", false);
        return p;
    }

    // ══════════════════════════════════════════════════════════════════
    // 고객 영역
    // ══════════════════════════════════════════════════════════════════

    /** UC-01 상영작 목록 — list.html 기존 완성 */
    @GetMapping("/movie/list")
    public String movieList(Model model) {
        return "movie/list";
    }

    /** UC-02 상영작 상세 */
    @GetMapping("/movie/{id}")
    public String movieDetail(@PathVariable Long id, Model model) {
        model.addAttribute("movie",     dummyMovie(id, "[미리보기] 더미 영화 #" + id, "15"));
        model.addAttribute("isSoldOut", false);
        return "movie/detail";
    }

    /** UC-03 날짜·시간·인원 선택 — schedule.html 기존 완성 */
    @GetMapping("/booking/schedule")
    public String bookingSchedule(Model model) {
        return "booking/schedule";
    }

    /** UC-03 좌석 선택 — seat.html 기존 완성 */
    @GetMapping("/booking/seat")
    public String bookingSeat(Model model) {
        return "booking/seat";
    }

    /** UC-04~06 결제·포인트 */
    @GetMapping("/payment/payment")
    public String paymentPayment(Model model) {
        model.addAttribute("movie",    dummyMovie(1L, "[미리보기] 더미 영화", "15"));
        model.addAttribute("schedule", new HashMap<String, Object>() {{
            put("id",        1L);
            put("no",        1);
            put("movieId",   1L);
            put("startTime", "2026-03-20T14:00");
            put("endTime",   "2026-03-20T16:10");
        }});
        model.addAttribute("theater",   dummyTheater(1));
        model.addAttribute("reservation", new HashMap<String, Object>() {{
            put("id",         "DUMMY-0001");
            put("scheduleId", 1L);
            put("seatNumber", List.of("C3", "C4"));
        }});
        model.addAttribute("adultCount",       2);
        model.addAttribute("teenCount",        0);
        model.addAttribute("discountPolicies", new ArrayList<>());
        model.addAttribute("bonusPolicies",    new ArrayList<>());
        model.addAttribute("member",           null);
        return "payment/payment";
    }

    /** UC-07 예매 완료 확인증 */
    @GetMapping("/payment/result")
    public String paymentResult(Model model) {
        model.addAttribute("payment", new HashMap<String, Object>() {{
            put("status",             "PAY");
            put("totalAmount",        30000);
            put("discountPolicyName", null);
            put("pointUsed",          0);
            put("pointEarned",        900);
        }});
        model.addAttribute("reservation", new HashMap<String, Object>() {{
            put("reservationId", "RES-20260320-001");
            put("seatNumbers",   List.of("C3", "C4"));
        }});
        model.addAttribute("movie",    dummyMovie(1L, "[미리보기] 더미 영화", "15"));
        model.addAttribute("schedule", new HashMap<String, Object>() {{
            put("startTime", "2026-03-20T14:00");
            put("endTime",   "2026-03-20T16:10");
        }});
        model.addAttribute("theater",  dummyTheater(1));
        return "payment/result";
    }

    // ══════════════════════════════════════════════════════════════════
    // 관리자 영역
    // ══════════════════════════════════════════════════════════════════

    /**
     * UC-11 관리자 로그인.
     * templates/admin/login/main.html
     */
    @GetMapping("/admin/login")
    public String adminLogin(Model model) {
        model.addAttribute("loginError", false);
        return "admin/login/main";
    }

    /**
     * 관리자 대시보드.
     * templates/admin/statistics/dashboard.html
     */
    @GetMapping("/admin/statistics/dashboard")
    public String adminDashboard(Model model) {
        model.addAttribute("todayStats", new HashMap<String, Object>() {{
            put("revenue",       1250000);
            put("customerCount", 83);
        }});
        return "admin/statistics/dashboard";
    }

    /**
     * UC-12 일일 통계.
     * templates/admin/statistics/stats/daily.html
     */
    @GetMapping("/admin/statistics/stats/daily")
    public String statsDaily(Model model) {
        model.addAttribute("statistics", List.of(
            dummyStat("2026-03-14", 800000,  53),
            dummyStat("2026-03-15", 1200000, 80),
            dummyStat("2026-03-16", 950000,  63),
            dummyStat("2026-03-17", 1500000, 100),
            dummyStat("2026-03-18", 1100000, 73),
            dummyStat("2026-03-19", 900000,  60),
            dummyStat("2026-03-20", 1250000, 83)
        ));
        model.addAttribute("startDate", "2026-03-14");
        model.addAttribute("endDate",   "2026-03-20");
        return "admin/statistics/stats/daily";
    }

    /**
     * UC-13 월별 통계.
     * templates/admin/statistics/stats/monthly.html
     */
    @GetMapping("/admin/statistics/stats/monthly")
    public String statsMonthly(Model model) {
        model.addAttribute("statistics", List.of(
            dummyStat("2026-01", 22000000, 1466),
            dummyStat("2026-02", 20500000, 1366),
            dummyStat("2026-03", 18000000, 1200)
        ));
        model.addAttribute("startDate", "2026-01-01");
        model.addAttribute("endDate",   "2026-03-31");
        return "admin/statistics/stats/monthly";
    }

    /**
     * UC-14 요일별 통계.
     * templates/admin/statistics/stats/by-day.html
     * StatisticsDTO.day: SUN / MON / TUE / WED / THU / FRI / SAT
     */
    @GetMapping("/admin/statistics/stats/by-day")
    public String statsByDay(Model model) {
        String[] days    = {"SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"};
        int[]    revenue = {1800000, 700000, 750000, 800000, 900000, 1600000, 2000000};
        int[]    count   = {120, 46, 50, 53, 60, 106, 133};

        List<Map<String, Object>> stats = new ArrayList<>();
        for (int i = 0; i < days.length; i++) {
            Map<String, Object> s = dummyStat(days[i], revenue[i], count[i]);
            s.put("day", days[i]); /* by-day 전용: day 필드 덮어쓰기 */
            stats.add(s);
        }
        model.addAttribute("statistics", stats);
        model.addAttribute("startDate", "2026-02-20");
        model.addAttribute("endDate",   "2026-03-20");
        return "admin/statistics/stats/by-day";
    }

    /**
     * UC-15 시간대별 통계.
     * templates/admin/statistics/stats/by-hour.html
     */
    @GetMapping("/admin/statistics/stats/by-hour")
    public String statsByHour(Model model) {
        int[][] hourData = {
            {10,400000,26}, {11,500000,33}, {12,700000,46},
            {13,600000,40}, {14,900000,60}, {15,950000,63},
            {16,1100000,73},{17,1200000,80},{18,1300000,86},
            {19,1500000,100},{20,1400000,93},{21,1100000,73},{22,600000,40}
        };
        List<Map<String, Object>> stats = new ArrayList<>();
        for (int[] h : hourData) {
            stats.add(dummyStat(h[0], h[1], h[2]));
        }
        model.addAttribute("statistics", stats);
        model.addAttribute("startDate", "2026-02-20");
        model.addAttribute("endDate",   "2026-03-20");
        return "admin/statistics/stats/by-hour";
    }

    /**
     * UC-16 영화별 통계.
     * templates/admin/statistics/stats/by-movie.html
     *
     * @param movieId  선택된 영화 ID (기본값 1)
     */
    @GetMapping("/admin/statistics/stats/by-movie")
    public String statsByMovie(
            @RequestParam(defaultValue = "1") Long movieId,
            Model model) {

        List<Map<String, Object>> movies = List.of(
            dummyMovie(1L, "[미리보기] 더미 영화 A", "15"),
            dummyMovie(2L, "[미리보기] 더미 영화 B", "ALL"),
            dummyMovie(3L, "[미리보기] 더미 영화 C", "19")
        );
        model.addAttribute("movies", movies);

        /* 선택된 영화 찾기 (없으면 첫 번째) */
        Map<String, Object> selected = movies.stream()
            .filter(m -> movieId.equals(((Number) m.get("movieId")).longValue()))
            .findFirst()
            .orElse(movies.get(0));
        model.addAttribute("selectedMovie", selected);

        model.addAttribute("statistics", List.of(
            dummyStat("2026-03-01", 1200000, 80),
            dummyStat("2026-03-08", 950000,  63),
            dummyStat("2026-03-15", 1500000, 100),
            dummyStat("2026-03-20", 1250000, 83)
        ));
        return "admin/statistics/stats/by-movie";
    }

    /**
     * UC-17 환불 처리.
     * templates/admin/management/refund.html
     * 초기 진입: reservation=null (조회 전 상태)
     */
    @GetMapping("/admin/refund")
    public String adminRefund(Model model) {
        model.addAttribute("reservation",  null);
        model.addAttribute("payment",      null);
        model.addAttribute("movie",        null);
        model.addAttribute("schedule",     null);
        model.addAttribute("isRefundable", false);
        return "admin/management/refund";
    }

    /**
     * UC-18~19 영화 등록·수정 폼.
     * templates/admin/management/movie/form.html
     * movieId 없으면 신규 등록, 있으면 수정 모드.
     *
     * @param movieId  수정 대상 ID (선택 파라미터)
     */
    @GetMapping("/admin/management/movie/form")
    public String adminMovieForm(
            @RequestParam(required = false) Long movieId,
            Model model) {

        model.addAttribute("movie", movieId != null
            ? dummyMovie(movieId, "[미리보기] 수정 대상 영화", "ALL")
            : null);

        model.addAttribute("theaters", List.of(
            dummyTheater(1),
            dummyTheater(2),
            dummyTheater(3)
        ));
        return "admin/management/movie/form";
    }

    /**
     * UC-20 영화 목록·상영 중지.
     * templates/admin/management/movie/manage.html
     */
    @GetMapping("/admin/management/movie/manage")
    public String adminMovieManage(Model model) {
        model.addAttribute("movies", List.of(
            dummyMovie(1L, "[미리보기] 더미 영화 A", "ALL"),
            dummyMovie(2L, "[미리보기] 더미 영화 B", "15"),
            dummyMovie(3L, "[미리보기] 더미 영화 C", "19")
        ));
        return "admin/management/movie/manage";
    }

    /**
     * UC-21 상영관 정보 수정.
     * templates/admin/management/theater/edit.html
     */
    @GetMapping("/admin/management/theater/edit")
    public String adminTheaterEdit(Model model) {
        List<Map<String, Object>> theaters = List.of(
            dummyTheater(1),
            dummyTheater(2),
            dummyTheater(3)
        );
        model.addAttribute("theaters", theaters);
        model.addAttribute("theater",  theaters.get(0));
        model.addAttribute("seatPolicies", List.of(
            dummySeatPolicy(1L, "일반석",     13000),
            dummySeatPolicy(2L, "장애인석",    9000),
            dummySeatPolicy(3L, "리클라이너", 19000)
        ));
        model.addAttribute("hasActiveReservation", true); /* 진행 중 예매 배너 확인용 */
        return "admin/management/theater/edit";
    }

    /**
     * 좌석 정보 수정 (팀 합의).
     * templates/admin/management/seat/edit.html
     *
     * @param policyId  조회할 좌석 정책 ID (기본값 1)
     */
    @GetMapping("/admin/management/seat/edit")
    public String adminSeatEdit(
            @RequestParam(defaultValue = "1") Long policyId,
            Model model) {

        model.addAttribute("theater", dummyTheater(1));
        model.addAttribute("seatPolicies", List.of(
            dummySeatPolicy(1L, "일반석",     13000),
            dummySeatPolicy(2L, "장애인석",    9000),
            dummySeatPolicy(3L, "리클라이너", 19000)
        ));

        /* 좌석 배치 더미 — 5행 × 8열 */
        List<Map<String, Object>> seats = new ArrayList<>();
        String[] rows = {"A", "B", "C", "D", "E"};
        for (String row : rows) {
            for (int col = 1; col <= 8; col++) {
                Map<String, Object> seat = new HashMap<>();
                seat.put("seatId",   row + col);
                seat.put("row",      row);
                seat.put("col",      col);
                seat.put("policyId", 1L);
                seats.add(seat);
            }
        }
        model.addAttribute("seats", seats);
        return "admin/management/seat/edit";
    }

    /**
     * 정책 목록 (팀 합의).
     * templates/admin/management/policy/list.html
     */
    @GetMapping("/admin/management/policy/list")
    public String adminPolicyList(Model model) {
        /* BonusPolicyDTO 더미 */
        model.addAttribute("bonusPolicies", List.of(
            new HashMap<String, Object>() {{
                put("id",         1L);
                put("policyName", "기본 적립");
                put("giveValue",  3.0);
            }},
            new HashMap<String, Object>() {{
                put("id",         2L);
                put("policyName", "특별 적립");
                put("giveValue",  5.0);
            }}
        ));
        /* DiscountPolicyDTO 더미 */
        model.addAttribute("discountPolicies", List.of(
            new HashMap<String, Object>() {{
                put("id",            1L);
                put("policyName",    "조조 할인");
                put("discountType",  "WON");
                put("discountValue", 3000);
                put("conditionType", "TIME");
            }},
            new HashMap<String, Object>() {{
                put("id",            2L);
                put("policyName",    "청소년 할인");
                put("discountType",  "RATIO");
                put("discountValue", 20.0);
                put("conditionType", "AGE");
            }}
        ));
        return "admin/management/policy/list";
    }

    /**
     * 정책 등록 (팀 합의).
     * templates/admin/management/policy/form.html
     * Model 없음 — 신규 등록 전용.
     */
    @GetMapping("/admin/management/policy/form")
    public String adminPolicyForm(Model model) {
        return "admin/management/policy/form";
    }

    /**
     * 정책 수정 (팀 합의).
     * templates/admin/management/policy/manage.html
     *
     * @param type  "bonus" | "discount" (기본값 "bonus")
     * @param id    수정할 정책 ID (기본값 1)
     */
    @GetMapping("/admin/management/policy/manage")
    public String adminPolicyManage(
            @RequestParam(defaultValue = "bonus") String type,
            @RequestParam(defaultValue = "1")     Long id,
            Model model) {

        model.addAttribute("policyType", type);

        if ("bonus".equals(type)) {
            model.addAttribute("bonusPolicy", new HashMap<String, Object>() {{
                put("id",         id);
                put("policyName", "[미리보기] 기본 적립");
                put("giveValue",  3.0);
            }});
            model.addAttribute("discountPolicy", null);
        } else {
            model.addAttribute("bonusPolicy", null);
            model.addAttribute("discountPolicy", new HashMap<String, Object>() {{
                put("id",            id);
                put("policyName",    "[미리보기] 조조 할인");
                put("discountType",  "WON");
                put("discountValue", 3000);
                put("conditionType", "TIME");
            }});
        }
        return "admin/management/policy/manage";
    }
}
