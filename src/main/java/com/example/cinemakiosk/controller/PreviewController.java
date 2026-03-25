package com.example.cinemakiosk.controller;

import com.example.cinemakiosk.domain.MovieEntity.MovieEntity;
import com.example.cinemakiosk.dto.MovieDTO;
import com.example.cinemakiosk.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * PreviewController — view 브랜치 전용 임시 컨트롤러
 * ─────────────────────────────────────────────────────────────────────────
 * 백엔드 작업 전 뷰 레이아웃 확인용. 실제 서비스 URL을 그대로 매핑하며,
 * Thymeleaf 렌더링 오류를 막기 위해 각 핸들러에서 더미 Model 데이터를 주입한다.
 *
 * ▶ 머지 시 이 파일 전체 삭제 후 실제 컨트롤러로 대체할 것.
 *
 * ▶ 접근 URL
 *   GET /
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

    // ── 실제 DB 조회용 Repository 주입 ──────────────────────────────────────
    // MovieService가 아직 없으므로 Repository 직접 사용. 머지 시 삭제.
    @Autowired
    private MovieRepository movieRepository;

    // ══════════════════════════════════════════════════════════════════
    // 헬퍼 — 더미 데이터 팩토리
    // ══════════════════════════════════════════════════════════════════

    /**
     * MovieDTO 더미 생성.
     *
     * ▶ startAt / endAt 을 LocalDate 타입으로 생성하는 이유:
     *   movie/detail.html 에서 Thymeleaf의 #temporals.format(movie.startAt, 'yyyy.MM.dd')
     *   를 사용하므로 String 이 아닌 LocalDate / LocalDateTime 타입이어야 한다.
     *   home.html, manage.html 은 th:text="${movie.startAt}" 단순 출력이므로
     *   LocalDate.toString() → "2026-03-01" 형식이라 표시상 문제없음.
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
        /* LocalDate 타입 — detail.html의 #temporals.format() 오류 방지 */
        m.put("startAt",     LocalDate.of(2026, 3, 1));
        m.put("endAt",       LocalDate.of(2026, 5, 31));
        m.put("posterUrl",   "/images/placeholder-poster.jpg");
        return m;
    }

    /**
     * StatisticsDTO 더미 생성.
     *
     * scheduleId 는 타입에 따라 다르게 사용됨:
     *   daily/monthly/by-movie → 날짜 문자열 ("2026-03-14", "2026-01" 등)
     *   by-hour                → 시간 숫자 (10, 11, ... )  ← hour 필드로도 별도 세팅
     *   by-day                 → 요일 코드 ("SUN" 등)      ← 호출부에서 day 필드 직접 세팅
     *
     * day 필드는 기본값 없음 — by-day 호출부에서 직접 put 할 것.
     * stats.js 레이블 우선순위: label > date > hour > day > scheduleId
     */
    private Map<String, Object> dummyStat(Object scheduleId, int revenue, int count) {
        Map<String, Object> s = new HashMap<>();
        s.put("id",            1);
        s.put("scheduleId",    scheduleId);
        /* day 기본값 제거 — by-day 가 아닌 타입에서 "MON→월" 레이블 오출력 방지 */
        s.put("revenue",       revenue);
        s.put("customerCount", count);
        return s;
    }

    /**
     * TheaterVO 더미 생성.
     * theater/edit.html, seat/edit.html, booking/seat.html 에서 공통 사용.
     *
     * ▶ theater 테이블에 name 컬럼 없음 (init.sql 기준).
     *   TheaterVO: no, policyId, cleanupTime 만 존재.
     *
     * @param no 상영관 번호
     */
    private Map<String, Object> dummyTheater(int no) {
        Map<String, Object> t = new HashMap<>();
        t.put("no",          no);
        /* name 제거 — DB theater 테이블에 name 컬럼 없음 */
        /* cost: 기본 좌석 요금 — seat.html에서 SEAT_COST 초기값으로 사용 */
        t.put("cost",        14000);
        t.put("cleanupTime", 20);
        return t;
    }

    /**
     * SeatPolicyVO 더미 생성.
     * theater/edit.html, seat/edit.html 에서 정책 목록으로 사용.
     *
     * ▶ DB seat_policy 테이블에 is_recliner 컬럼 없음 (init.sql 기준).
     *   SeatPolicyVO: policyId, name, cost 만 존재.
     *   리클라이너 여부는 name 에 "리클라이너" 포함 여부로 View 에서 판단.
     *
     * @param policyId 정책 ID (CHAR(36) UUID 형식)
     * @param name     정책명 — "리클라이너" 포함 시 View 체크박스 자동 체크
     * @param cost     요금
     */
    private Map<String, Object> dummySeatPolicy(String policyId, String name, int cost) {
        Map<String, Object> p = new HashMap<>();
        p.put("policyId", policyId);
        p.put("name",     name);
        p.put("cost",     cost);
        /* isRecliner 제거 — DB 컬럼 없음, name으로 판단 */
        return p;
    }

    // ══════════════════════════════════════════════════════════════════
    // 고객 영역
    // ══════════════════════════════════════════════════════════════════

    /**
     * 홈(스플래시) 화면 — 상영 예정 + 상영 중 영화 슬라이드쇼.
     * templates/home.html
     *
     * ▶ 실제 컨트롤러 작성 시
     *   MovieService에서 status가 UPCOMING / NOW인 영화만 조회하여
     *   List<MovieDTO> 형태로 "movies" 키로 모델에 담을 것.
     */
    @GetMapping("/")
    public String home(Model model) {
        /* 상영 예정 영화 2편 — endAt=null 이면 home.html에서 "개봉 예정" 표시 */
        List<Map<String, Object>> movies = new ArrayList<>();

        Map<String, Object> u1 = new HashMap<>();
        u1.put("movieId",     4L);
        u1.put("title",       "아바타: 새벽의 땅");
        u1.put("genre",       "SF / 어드벤처");
        u1.put("rating",      "ALL");
        u1.put("runtime",     168);
        u1.put("director",    "제임스 카메론");
        u1.put("actor",       "샘 워싱턴, 조 살다나");
        u1.put("description", "판도라 행성의 세 번째 이야기.");
        u1.put("startAt",     LocalDate.of(2026, 4, 15));
        u1.put("endAt",       null);   /* null → home.html에서 "YYYY-MM-DD 개봉 예정" 표시 */
        u1.put("posterUrl",   "/images/placeholder-poster.jpg");
        movies.add(u1);

        Map<String, Object> u2 = new HashMap<>();
        u2.put("movieId",     5L);
        u2.put("title",       "미션 임파서블: 파이널 레코닝");
        u2.put("genre",       "액션 / 첩보");
        u2.put("rating",      "15");
        u2.put("runtime",     145);
        u2.put("director",    "크리스토퍼 맥쿼리");
        u2.put("actor",       "톰 크루즈, 헤일리 앳웰");
        u2.put("description", "에단 헌트의 마지막 미션.");
        u2.put("startAt",     LocalDate.of(2026, 5, 20));
        u2.put("endAt",       null);
        u2.put("posterUrl",   "/images/placeholder-poster.jpg");
        movies.add(u2);

        /* 현재 상영 중 영화 3편 */
        Map<String, Object> n1 = dummyMovie(1L, "범죄도시 5", "15");
        n1.put("genre",   "액션 / 범죄");
        n1.put("startAt", LocalDate.of(2026, 3, 12));
        n1.put("endAt",   LocalDate.of(2026, 5, 11));
        movies.add(n1);

        Map<String, Object> n2 = dummyMovie(2L, "기생충: 리턴", "15");
        n2.put("genre",   "스릴러 / 드라마");
        n2.put("startAt", LocalDate.of(2026, 3, 5));
        n2.put("endAt",   LocalDate.of(2026, 4, 30));
        movies.add(n2);

        Map<String, Object> n3 = dummyMovie(3L, "용감한 시민들", "ALL");
        n3.put("genre",   "액션 / 코미디");
        n3.put("startAt", LocalDate.of(2026, 2, 25));
        n3.put("endAt",   LocalDate.of(2026, 4, 20));
        movies.add(n3);

        model.addAttribute("movies", movies);
        return "home";
    }

    /**
     * UC-01 상영작 목록.
     * templates/movie/list.html
     *
     * ▶ list.html은 완전히 JS(fetchMovies()) 기반 렌더링이므로
     *   SSR Model 주입 불필요. 필터·카드 생성 모두 list.js 담당.
     */
    @GetMapping("/movie/list")
    public String movieList(Model model) {
        return "movie/list";
    }

    /**
     * UC-01 영화 목록 API — 실제 DB 조회.
     * ─────────────────────────────────────────────────────────────────────────
     * list.js 의 fetchMovies() → GET /api/movies?status=NOW|UPCOMING 호출에 응답.
     *
     * ▶ NOW     : MovieRepository.findNowPlaying() 사용 (startAt <= now <= endAt)
     * ▶ UPCOMING: startAt > now 인 영화를 findAll() 후 stream 필터링
     *             (MovieRepository에 별도 쿼리 없으므로 임시 처리)
     *
     * ▶ posterUrl / soldOut 은 MovieDTO에 없는 필드 → list.js 기본값(placeholder/false)으로 처리됨.
     * ▶ 실제 API 연동 시 이 메서드 삭제할 것.
     *
     * @param status 'NOW' | 'UPCOMING'
     * @return MovieDTO 리스트 (JSON)
     */
    @GetMapping("/api/movies")
    @ResponseBody
    public List<MovieDTO> apiMovieList(
            @RequestParam(defaultValue = "NOW") String status) {

        LocalDateTime now = LocalDateTime.now();
        List<MovieEntity> entities;

        if ("NOW".equalsIgnoreCase(status)) {
            /* 현재 상영 중: startAt <= now AND endAt >= now */
            entities = movieRepository.findAll();
        } else {
            /* 상영 예정: startAt > now */
            entities = movieRepository.findAll().stream()
                    .filter(m -> m.getStartAt() != null && m.getStartAt().isAfter(now))
                    .collect(Collectors.toList());
        }

        return entities.stream()
                .map(MovieDTO::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * UC-03 좌석 상태 API — 더미 데이터 반환.
     * ─────────────────────────────────────────────────────────────────────────
     * seat.js 의 fetchSeats() → GET /api/seats?scheduleId= 호출에 응답.
     * 5행(A~E) × 8열 = 40석 구성. E행은 예매 완료(TAKEN) 처리로 다양한 상태 시연.
     *
     * ▶ 실제 API 연동 시 이 메서드 삭제할 것.
     *
     * @param scheduleId 상영 스케줄 ID (현재 더미에서는 미사용)
     * @return 좌석 상태 목록 (JSON)
     */
    @GetMapping("/api/seats")
    @ResponseBody
    public List<Map<String, Object>> apiSeatList(
            @RequestParam(defaultValue = "0") Long scheduleId) {

        List<Map<String, Object>> seats = new ArrayList<>();
        String[] rows = {"A", "B", "C", "D", "E"};

        for (String row : rows) {
            for (int col = 1; col <= 8; col++) {
                Map<String, Object> seat = new HashMap<>();
                seat.put("seatId", row + col);
                seat.put("row",    row);
                seat.put("col",    col);

                /* E행 전체 예매 완료, C3·C4 임시 점유, 나머지 선택 가능 */
                String status;
                if ("E".equals(row)) {
                    status = "TAKEN";
                } else if ("C".equals(row) && (col == 3 || col == 4)) {
                    status = "SELECTING";
                } else {
                    status = "AVAILABLE";
                }
                seat.put("status", status);
                seats.add(seat);
            }
        }
        return seats;
    }

    /**
     * UC-02 상영작 상세 — 실제 DB 조회.
     * templates/movie/detail.html
     *
     * ▶ MovieRepository.findById(id) 로 조회.
     *   없는 id 접근 시 더미 데이터로 fallback (개발 편의용).
     *
     * ▶ isSoldOut : 매진 판단 로직 미구현 → 임시 false 고정.
     *               실제 구현 시 Schedule 조회 후 잔여 좌석 여부로 판단.
     * ▶ posterUrl : MovieEntity에 없는 필드 → placeholder 고정.
     *               실제 구현 시 파일 서버 URL 또는 DB 컬럼 추가 필요.
     */
    @GetMapping("/movie/{id}")
    public String movieDetail(@PathVariable Long id, Model model) {
        MovieEntity entity = movieRepository.findById(id).orElse(null);

        if (entity == null) {
            /* id에 해당하는 영화 없음 → 더미 fallback */
            model.addAttribute("movie",     dummyMovie(id, "[더미] 영화 #" + id, "15"));
        } else {
            model.addAttribute("movie",     MovieDTO.toDTO(entity));
        }

        /* TODO: 매진 판단 로직 — Schedule/좌석 조회 후 교체 */
        model.addAttribute("isSoldOut", false);
        /* TODO: 포스터 URL — 파일 서버 연동 후 교체 */
        model.addAttribute("posterUrl", "/images/placeholder-poster.jpg");

        return "movie/detail";
    }

    /**
     * UC-03 날짜·시간·인원 선택.
     * templates/booking/schedule.html
     *
     * ▶ 주요 Model 속성
     *   - movie       : MovieDTO          영화 정보 (제목·등급·런타임 표시용)
     *   - posterUrl   : String            포스터 URL (별도 attribute)
     *   - schedules   : List<ScheduleDTO> 상영 스케줄 목록
     *                   └ id, no, movieId, startTime(ISO 문자열), endTime(ISO 문자열)
     *   - theaterCost : Integer           기본 좌석 요금
     *
     * ▶ schedules 의 startTime/endTime 을 ISO 문자열로 설정하는 이유:
     *   th:inline="javascript" 로 직렬화 시 LocalDateTime 은 배열([2026,3,22,...])로
     *   변환되어 JS에서 파싱 불가. ISO 문자열 "2026-03-22T14:00:00" 형태가 안전.
     */
    @GetMapping("/booking/schedule")
    public String bookingSchedule(Model model) {
        /* 예매 대상 영화 더미 */
        model.addAttribute("movie",       dummyMovie(1L, "[미리보기] 범죄도시 5", "15"));
        model.addAttribute("posterUrl",   "/images/placeholder-poster.jpg");
        /* 기본 좌석 요금 (2025 CGV 기준 일반 14,000원) */
        model.addAttribute("theaterCost", 14000);

        /* 오늘 기준 3일치, 하루 4회차 스케줄 생성 */
        List<Map<String, Object>> schedules = new ArrayList<>();
        /* 회차별 [시작시, 시작분, 런타임(분)] */
        int[][] timeSlots = {{10, 0, 120}, {14, 0, 120}, {17, 30, 120}, {20, 0, 120}};
        LocalDate today   = LocalDate.now();
        long schedId      = 1L;

        for (int day = 0; day < 3; day++) {
            for (int[] slot : timeSlots) {
                LocalDateTime start = today.plusDays(day)
                        .atTime(slot[0], slot[1]);
                LocalDateTime end   = start.plusMinutes(slot[2]);

                Map<String, Object> s = new HashMap<>();
                s.put("id",        schedId++);
                s.put("no",        (day % 2) + 1);        /* 1관 또는 2관 교대 */
                s.put("movieId",   1L);
                /* ISO 문자열 — schedule.html JS에서 Date 파싱에 사용 */
                s.put("startTime", start.toString());
                s.put("endTime",   end.toString());
                schedules.add(s);
            }
        }
        model.addAttribute("schedules", schedules);
        return "booking/schedule";
    }

    /**
     * UC-03 좌석 선택.
     * templates/booking/seat.html
     *
     * ▶ 주요 Model 속성
     *   - schedule   : ScheduleDTO  선택된 스케줄 (startTime/endTime — ISO 문자열)
     *   - theater    : TheaterDTO   상영관 정보 (cost → SEAT_COST 에 사용)
     *   - movie      : MovieDTO     예매 대상 영화 요약 (제목·등급 표시)
     *   - adultCount : Integer      성인 인원 수
     *   - teenCount  : Integer      청소년 인원 수
     *
     * ▶ schedule.startTime/endTime 을 ISO 문자열로 설정하는 이유:
     *   seat.html → th:data-start="${schedule.startTime}" 으로 JS에 전달되며
     *   JS에서 new Date(startTime)으로 파싱하므로 ISO 문자열이 적합.
     */
    @GetMapping("/booking/seat")
    public String bookingSeat(
            @RequestParam(required = false)                    Long    movieId,
            @RequestParam(required = false, defaultValue = "0") Long    scheduleId,
            @RequestParam(required = false, defaultValue = "1") Integer adultCount,
            @RequestParam(required = false, defaultValue = "0") Integer teenCount,
            Model model) {

        /* ── 상영 스케줄 (scheduleId가 있으면 사용, 없으면 더미) ─────────── */
        Map<String, Object> schedule = new HashMap<>();
        schedule.put("id",        scheduleId);
        schedule.put("no",        1);
        schedule.put("movieId",   movieId != null ? movieId : 1L);
        /* ISO 문자열 — seat.html의 data-start / data-end attribute 및 JS 파싱에 사용 */
        schedule.put("startTime", LocalDate.now().atTime(14, 0).toString());
        schedule.put("endTime",   LocalDate.now().atTime(16, 10).toString());

        /* ── 영화 정보: movieId가 있으면 DB 조회, 없으면 더미 fallback ───── */
        // Optional<MovieDTO> vs Map<String,Object> 타입 혼용으로 람다 체이닝 불가.
        // 명시적 분기로 처리.
        Object movieModel;
        if (movieId != null) {
            java.util.Optional<MovieEntity> found = movieRepository.findById(movieId);
            movieModel = found.isPresent()
                    ? MovieDTO.toDTO(found.get())
                    : dummyMovie(movieId, "[미리보기] 더미 영화", "15");
        } else {
            movieModel = dummyMovie(1L, "[미리보기] 더미 영화", "15");
        }

        model.addAttribute("schedule",   schedule);
        model.addAttribute("theater",    dummyTheater(1));
        model.addAttribute("movie",      movieModel);
        model.addAttribute("adultCount", adultCount);
        model.addAttribute("teenCount",  teenCount);
        return "booking/seat";
    }

    /**
     * UC-04~06 결제·포인트.
     * templates/payment/payment.html
     *
     * ▶ 주요 Model 속성
     *   - movie            : MovieDTO              영화 요약
     *   - schedule         : ScheduleDTO           상영 스케줄 (startTime/endTime — ISO 문자열)
     *   - theater          : TheaterDTO            상영관 (cost → SEAT_COST)
     *   - reservation      : ReservationDetailsDTO 예매 내역 (seatNumber — List)
     *   - discountPolicies : List<DiscountPolicyDTO> 할인 정책 목록
     *   - bonusPolicies    : List<BonusPolicyDTO>   적립 정책 목록
     *   - adultCount       : Integer
     *   - teenCount        : Integer
     *   - member           : MembersDTO or null     포인트 인증 완료 회원 (미인증 시 null)
     */
    @GetMapping("/payment/payment")
    public String paymentPayment(Model model) {
        model.addAttribute("movie", dummyMovie(1L, "[미리보기] 더미 영화", "15"));

        /* 상영 스케줄 더미 — payment.html은 th:data-start/end 로만 참조하므로 ISO 문자열 OK */
        Map<String, Object> schedule = new HashMap<>();
        schedule.put("id",        1L);
        schedule.put("no",        1);
        schedule.put("movieId",   1L);
        schedule.put("startTime", LocalDate.now().atTime(14, 0).toString());
        schedule.put("endTime",   LocalDate.now().atTime(16, 10).toString());
        model.addAttribute("schedule", schedule);

        model.addAttribute("theater", dummyTheater(1));

        /* reservation.seatNumber 는 List<String> 타입 — payment.html th:each 로 반복 */
        Map<String, Object> reservation = new HashMap<>();
        reservation.put("id",         "DUMMY-0001");
        reservation.put("scheduleId", 1L);
        reservation.put("seatNumber", List.of("C3", "C4"));
        model.addAttribute("reservation", reservation);

        model.addAttribute("adultCount", 2);
        model.addAttribute("teenCount",  0);

        /* 할인 정책 더미 — 조조·청소년 할인 (dummy_data.sql 기준) */
        model.addAttribute("discountPolicies", List.of(
            new HashMap<String, Object>() {{
                put("id",            1L);
                put("policyName",    "조조 할인");
                put("discountType",  "WON");
                put("discountValue", 4000);
                put("conditionType", "TIME");
            }},
            new HashMap<String, Object>() {{
                put("id",            2L);
                put("policyName",    "청소년 할인");
                put("discountType",  "WON");
                put("discountValue", 3000);
                put("conditionType", "AGE");
            }}
        ));

        /* 적립 정책 더미 — 기본 1% 적립 */
        model.addAttribute("bonusPolicies", List.of(
            new HashMap<String, Object>() {{
                put("id",         1L);
                put("policyName", "기본 적립");
                put("giveValue",  1);   /* 1% */
            }}
        ));

        /* 미인증 상태 (null) — 인증 후에는 phone, point 포함된 객체 주입 */
        model.addAttribute("member", null);
        return "payment/payment";
    }

    /**
     * UC-07 예매 완료 확인증.
     * templates/payment/result.html
     *
     * ▶ 주요 Model 속성 (result.html 기준 — 이전 버전과 키 불일치 수정)
     *   - payment      : PaymentDetailsDTO
     *                    └ cost(Long), usePoint(Long), time(LocalDateTime), status
     *   - reservation  : ReservationDetailsDTO
     *                    └ id(String), seatNumber(List<String>)
     *   - movie        : MovieDTO          영화 제목·등급
     *   - schedule     : ScheduleDTO       startTime(LocalDateTime), no(int)
     *   - earnedPoints : Long              이번 결제로 적립된 포인트 (별도 attribute)
     *
     * ▶ payment.time, schedule.startTime 을 LocalDateTime 으로 설정하는 이유:
     *   result.html → #temporals.format(payment.time, 'yyyy.MM.dd HH:mm')
     *              → #temporals.format(schedule.startTime, 'yyyy.MM.dd HH:mm')
     *   Thymeleaf #temporals 유틸은 LocalDate/LocalDateTime 타입만 처리 가능.
     *   String 으로 설정하면 TemplateProcessingException 발생.
     *
     * ▶ [수정 이력]
     *   - payment.totalAmount  → payment.cost       (result.html 기준 키)
     *   - payment.pointUsed    → payment.usePoint   (result.html 기준 키)
     *   - reservation.reservationId → reservation.id
     *   - reservation.seatNumbers   → reservation.seatNumber (List<String>)
     *   - earnedPoints 별도 attribute 추가 (이전엔 payment.pointEarned 로 잘못 설정)
     */
    @GetMapping("/payment/result")
    public String paymentResult(Model model) {
        /* 결제 내역 — result.html 참조 키와 정확히 일치시킬 것 */
        Map<String, Object> payment = new HashMap<>();
        payment.put("cost",      28000);                              /* 기본 요금 2인(14000×2) */
        payment.put("usePoint",  0L);                                 /* 포인트 미사용 */
        payment.put("time",      LocalDateTime.of(2026, 3, 20, 14, 35)); /* #temporals.format() 사용 */
        payment.put("status",    "PAY");
        model.addAttribute("payment", payment);

        /* 예매 내역 — reservation.id, reservation.seatNumber 로 참조 */
        Map<String, Object> reservation = new HashMap<>();
        reservation.put("id",         "RES-20260320-001");
        reservation.put("seatNumber", List.of("C3", "C4"));
        model.addAttribute("reservation", reservation);

        model.addAttribute("movie", dummyMovie(1L, "[미리보기] 더미 영화", "15"));

        /* 상영 스케줄 — startTime: LocalDateTime (result.html에서 #temporals.format 사용) */
        Map<String, Object> schedule = new HashMap<>();
        schedule.put("startTime", LocalDateTime.of(2026, 3, 20, 14, 0)); /* #temporals 처리 */
        schedule.put("no",        1);
        model.addAttribute("schedule", schedule);

        /* earnedPoints: payment.html의 BONUS_POLICIES(1%) 기준 → 28000 × 1% = 280P */
        model.addAttribute("earnedPoints", 280L);
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
     *
     * ▶ todayStats.revenue, todayStats.customerCount 사용
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
     *
     * ▶ STAT_TYPE = 'daily' 는 템플릿 내 인라인 JS에서 직접 설정
     * ▶ statistics, startDate, endDate 를 Model 로 주입
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
     *
     * ▶ StatisticsDTO.day: SUN / MON / TUE / WED / THU / FRI / SAT
     * ▶ by-day 전용으로 day 필드를 직접 put — dummyStat()에는 day 기본값 없음
     */
    @GetMapping("/admin/statistics/stats/by-day")
    public String statsByDay(Model model) {
        String[] days    = {"SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"};
        int[]    revenue = {1800000, 700000, 750000, 800000, 900000, 1600000, 2000000};
        int[]    count   = {120, 46, 50, 53, 60, 106, 133};

        List<Map<String, Object>> stats = new ArrayList<>();
        for (int i = 0; i < days.length; i++) {
            Map<String, Object> s = dummyStat(days[i], revenue[i], count[i]);
            /* by-day 전용 day 필드 덮어쓰기 — stats.js 레이블 우선순위에서 day 필드 참조 */
            s.put("day", days[i]);
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
     *
     * ▶ hour 필드 추가 — stats.js 레이블 우선순위: label > date > hour > day > scheduleId
     *   → by-hour 차트 X축에 "10시", "11시" 형태로 표시
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
            Map<String, Object> s = dummyStat(h[0], h[1], h[2]);
            /* hour 필드 추가 — stats.js 레이블: stat.hour + "시" → "10시", "11시" ... */
            s.put("hour", h[0]);
            stats.add(s);
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
     * ▶ 주요 Model 속성
     *   - movies        : List<MovieDTO>      영화 선택 드롭다운용 목록
     *   - selectedMovie : MovieDTO            현재 선택된 영화
     *   - statistics    : List<StatisticsDTO> 선택 영화의 통계 (날짜별)
     *
     * @param movieId 선택된 영화 ID (기본값 1)
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

        /* 선택 영화의 날짜별 통계 (4주치) */
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
     *
     * ▶ 초기 진입: reservation=null (조회 전 상태)
     *   refund.html의 #refundResult 가 is-hidden 클래스 적용됨
     *
     * ▶ [백엔드 주의] refund.html에서 payment.totalAmount 를 참조하는데
     *   DB 스키마의 payment_details 컬럼은 'cost' 임.
     *   실제 컨트롤러 작성 시 PaymentDetailsDTO에 totalAmount 필드를
     *   추가하거나 템플릿을 payment.cost 로 수정할 것.
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
     *
     * ▶ movieId 없으면 신규 등록(movie=null), 있으면 수정 모드(movie 주입)
     * ▶ theaters 목록: theaterNo select 와 cleanupTime 힌트에 사용
     *
     * @param movieId 수정 대상 ID (선택 파라미터)
     */
    @GetMapping("/admin/management/movie/form")
    public String adminMovieForm(
            @RequestParam(required = false) Long movieId,
            Model model) {

        model.addAttribute("movie", movieId != null
            ? dummyMovie(movieId, "[미리보기] 수정 대상 영화", "ALL")
            : null);

        /* theaters: form.html의 상영관 선택 드롭다운 + cleanupTime 힌트 */
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
     *
     * ▶ manage.html에서 movie.startAt/endAt 을 단순 문자열로 출력하므로
     *   LocalDate.toString() → "2026-03-01" 형태로 표시됨
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
     *
     * ▶ seat_policy.name 으로 리클라이너 여부 구분 ("리클라이너" 포함 여부).
     *   policyId 는 CHAR(36) UUID 형식 (init.sql dummy_data.sql 기준).
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
        /* policyId: init.sql/dummy_data.sql의 UUID 형식에 맞춤 */
        model.addAttribute("seatPolicies", List.of(
            dummySeatPolicy("sp001000-0000-0000-0000-000000000001", "일반석",    14000),
            dummySeatPolicy("sp002000-0000-0000-0000-000000000002", "리클라이너", 25000),
            dummySeatPolicy("sp003000-0000-0000-0000-000000000003", "특별관석",  18000)
        ));
        /* hasActiveReservation=true → edit.html에서 진행 중 예매 경고 배너 표시 */
        model.addAttribute("hasActiveReservation", true);
        return "admin/management/theater/edit";
    }

    /**
     * 좌석 정보 수정.
     * templates/admin/management/seat/edit.html
     *
     * ▶ 주요 Model 속성
     *   - theater      : TheaterVO            상영관 번호 (no, policyId, cleanupTime)
     *   - seatPolicies : List<SeatPolicyVO>   정책 목록 (policyId, name, cost)
     *   - seats        : List<SeatStatusDTO>  좌석 배치 (행·열·현재 정책)
     *
     * @param policyId 조회할 좌석 정책 ID (기본값, 현재 더미에서는 미사용)
     */
    @GetMapping("/admin/management/seat/edit")
    public String adminSeatEdit(
            @RequestParam(defaultValue = "sp001000-0000-0000-0000-000000000001") String policyId,
            Model model) {

        model.addAttribute("theater", dummyTheater(1));
        model.addAttribute("seatPolicies", List.of(
            dummySeatPolicy("sp001000-0000-0000-0000-000000000001", "일반석",    14000),
            dummySeatPolicy("sp002000-0000-0000-0000-000000000002", "리클라이너", 25000),
            dummySeatPolicy("sp003000-0000-0000-0000-000000000003", "특별관석",  18000)
        ));

        /* 좌석 배치 더미 — 5행 × 8열 = 40석 */
        List<Map<String, Object>> seats = new ArrayList<>();
        String[] rows = {"A", "B", "C", "D", "E"};
        for (String row : rows) {
            for (int col = 1; col <= 8; col++) {
                Map<String, Object> seat = new HashMap<>();
                seat.put("seatId",   row + col);
                seat.put("row",      row);
                seat.put("col",      col);
                /* E행은 리클라이너, 나머지는 일반석 */
                seat.put("policyId", row.equals("E") ? 3L : 1L);
                seats.add(seat);
            }
        }
        model.addAttribute("seats", seats);
        return "admin/management/seat/edit";
    }

    /**
     * 정책 목록.
     * templates/admin/management/policy/list.html
     *
     * ▶ bonusPolicies: giveValue 는 퍼센트(%) 단위
     *   list.html → th:text="${policy.giveValue + '%'}" 로 표시
     * ▶ discountPolicies: discountType(RATIO/WON), conditionType(TIME/AGE/JOB/COUPON)
     *   dummy_data.sql 의 실제 데이터와 일치시킴
     */
    @GetMapping("/admin/management/policy/list")
    public String adminPolicyList(Model model) {
        /* 적립 정책 더미 (dummy_data.sql 기준) */
        model.addAttribute("bonusPolicies", List.of(
            new HashMap<String, Object>() {{
                put("id",         1L);
                put("policyName", "기본 적립");
                put("giveValue",  1);   /* 1% */
            }},
            new HashMap<String, Object>() {{
                put("id",         2L);
                put("policyName", "봄 시즌 적립");
                put("giveValue",  2);   /* 2% */
            }},
            new HashMap<String, Object>() {{
                put("id",         3L);
                put("policyName", "여름 특별 적립");
                put("giveValue",  3);   /* 3% */
            }}
        ));
        /* 할인 정책 더미 (dummy_data.sql 기준 절대금액 WON 방식) */
        model.addAttribute("discountPolicies", List.of(
            new HashMap<String, Object>() {{
                put("id",            1L);
                put("policyName",    "조조 할인");
                put("discountType",  "WON");
                put("discountValue", 4000);
                put("conditionType", "TIME");
            }},
            new HashMap<String, Object>() {{
                put("id",            2L);
                put("policyName",    "청소년 할인");
                put("discountType",  "WON");
                put("discountValue", 3000);
                put("conditionType", "AGE");
            }},
            new HashMap<String, Object>() {{
                put("id",            3L);
                put("policyName",    "군·경 할인");
                put("discountType",  "WON");
                put("discountValue", 4000);
                put("conditionType", "JOB");
            }},
            new HashMap<String, Object>() {{
                put("id",            4L);
                put("policyName",    "쿠폰 할인");
                put("discountType",  "WON");
                put("discountValue", 2000);
                put("conditionType", "COUPON");
            }},
            new HashMap<String, Object>() {{
                put("id",            5L);
                put("policyName",    "봄맞이 주중 할인");
                put("discountType",  "WON");
                put("discountValue", 2000);
                put("conditionType", "TIME");
            }}
        ));
        return "admin/management/policy/list";
    }

    /**
     * 정책 등록.
     * templates/admin/management/policy/form.html
     *
     * ▶ 신규 등록 전용 — SSR Model 불필요.
     */
    @GetMapping("/admin/management/policy/form")
    public String adminPolicyForm(Model model) {
        return "admin/management/policy/form";
    }

    /**
     * 정책 수정.
     * templates/admin/management/policy/manage.html
     *
     * ▶ type = "bonus"    → bonusPolicy 주입,    discountPolicy=null
     *   type = "discount" → discountPolicy 주입, bonusPolicy=null
     *
     * @param type "bonus" | "discount" (기본값 "bonus")
     * @param id   수정할 정책 ID (기본값 1)
     */
    @GetMapping("/admin/management/policy/manage")
    public String adminPolicyManage(
            @RequestParam(defaultValue = "bonus")   String type,
            @RequestParam(defaultValue = "1")        Long id,
            Model model) {

        model.addAttribute("policyType", type);

        if ("bonus".equals(type)) {
            model.addAttribute("bonusPolicy", new HashMap<String, Object>() {{
                put("id",         id);
                put("policyName", "[미리보기] 기본 적립");
                put("giveValue",  1);   /* 1% */
            }});
            model.addAttribute("discountPolicy", null);
        } else {
            model.addAttribute("bonusPolicy", null);
            model.addAttribute("discountPolicy", new HashMap<String, Object>() {{
                put("id",            id);
                put("policyName",    "[미리보기] 조조 할인");
                put("discountType",  "WON");
                put("discountValue", 4000);
                put("conditionType", "TIME");
            }});
        }
        return "admin/management/policy/manage";
    }
}
