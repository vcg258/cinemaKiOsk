/**
 * home.js — 키오스크 홈(스플래시) 화면 동작 스크립트
 * 파일 위치 : static/js/home.js
 * 대응 템플릿: templates/home.html
 *
 * 담당 기능
 *   1. 슬라이드쇼 자동 전환 (크로스페이드 + 인디케이터 동기화)
 *   2. 화면 어디든 터치(클릭) → /movie/list 이동
 *   3. 관리자 버튼 5회 연속 탭 → /admin/login 이동
 *      (3초 이상 탭 간격이 벌어지면 카운터 리셋)
 */

(function () {
  'use strict';

  /* ─────────────────────────────────────────────────────────────────────
   * 설정 상수
   * ───────────────────────────────────────────────────────────────────── */

  /** 슬라이드 자동 전환 간격 (ms) */
  const SLIDE_INTERVAL_MS = 5000;

  /** 관리자 버튼 — 이동에 필요한 연속 탭 횟수 */
  const ADMIN_TAP_REQUIRED = 5;

  /** 관리자 버튼 — 마지막 탭 이후 카운터 리셋까지 대기 시간 (ms) */
  const ADMIN_TAP_RESET_MS = 3000;

  /** 이동할 URL 상수 */
  const URL_MOVIE_LIST  = '/movie/list';
  const URL_ADMIN_LOGIN = '/admin/login';


  /* ─────────────────────────────────────────────────────────────────────
   * DOM 참조
   * ───────────────────────────────────────────────────────────────────── */

  /** 모든 .slide 요소 */
  const slides = document.querySelectorAll('.slide');

  /** 모든 .indicator 요소 */
  const indicators = document.querySelectorAll('.indicator');

  /** 관리자 버튼 */
  const adminBtn = document.getElementById('admin-btn');

  /** 관리자 버튼 내 탭 카운트 도트 목록 */
  const dots = adminBtn ? adminBtn.querySelectorAll('.admin-btn__dot') : [];


  /* ─────────────────────────────────────────────────────────────────────
   * 슬라이드쇼
   * ───────────────────────────────────────────────────────────────────── */

  /** 현재 활성화된 슬라이드 인덱스 */
  let currentIndex = 0;

  /** setInterval ID (클리어 시 사용) */
  let slideIntervalId = null;

  /**
   * 특정 인덱스의 슬라이드로 전환.
   * — 이전 슬라이드: slide--active 제거 (페이드 아웃)
   * — 다음 슬라이드: slide--active 추가 (페이드 인)
   * — 인디케이터: indicator--active 동기화
   *
   * @param {number} index - 이동할 슬라이드 인덱스 (자동 순환 처리)
   */
  function goToSlide(index) {
    if (slides.length === 0) return;

    /* 순환 인덱스 계산 */
    const next = ((index % slides.length) + slides.length) % slides.length;

    /* 현재 슬라이드 비활성화 */
    slides[currentIndex].classList.remove('slide--active');
    if (indicators[currentIndex]) {
      indicators[currentIndex].classList.remove('indicator--active');
    }

    /* 다음 슬라이드 활성화 */
    slides[next].classList.add('slide--active');
    if (indicators[next]) {
      indicators[next].classList.add('indicator--active');
    }

    currentIndex = next;
  }

  /**
   * 자동 슬라이드 전환 시작.
   * 슬라이드가 2개 이상일 때만 활성화.
   */
  function startAutoSlide() {
    if (slides.length <= 1) return;
    slideIntervalId = setInterval(function () {
      goToSlide(currentIndex + 1);
    }, SLIDE_INTERVAL_MS);
  }

  /* 슬라이드쇼 초기화 */
  if (slides.length > 0) {
    startAutoSlide();
  }


  /* ─────────────────────────────────────────────────────────────────────
   * 화면 터치 → /movie/list 이동
   *
   * document 레벨에서 click 이벤트를 감지.
   * 관리자 버튼은 아래에서 e.stopPropagation() 처리하여 이 핸들러를 건너뜀.
   * ───────────────────────────────────────────────────────────────────── */
  document.addEventListener('click', function handleScreenTouch() {
    window.location.href = URL_MOVIE_LIST;
  });


  /* ─────────────────────────────────────────────────────────────────────
   * 관리자 버튼 — 5회 연속 탭 카운터
   * ───────────────────────────────────────────────────────────────────── */

  /** 현재 누적 탭 횟수 */
  let tapCount = 0;

  /** 리셋 대기 타이머 ID */
  let tapResetTimerId = null;

  /**
   * 탭 횟수에 맞게 도트 UI 업데이트.
   * — count개 도트를 브랜드 골드로 채우고, 나머지는 빈 상태로 표시.
   *
   * @param {number} count - 채울 도트 수 (0 이상 5 이하)
   */
  function updateDots(count) {
    dots.forEach(function (dot, i) {
      /* i < count 이면 채워진 상태, 아니면 빈 상태 */
      dot.classList.toggle('admin-btn__dot--filled', i < count);
    });
  }

  if (adminBtn) {
    adminBtn.addEventListener('click', function handleAdminTap(e) {
      /*
       * 이벤트 버블링 중단:
       * document의 handleScreenTouch 핸들러가 실행되지 않도록 함.
       * 관리자 버튼 클릭은 /movie/list가 아닌 이 핸들러에서만 처리.
       */
      e.stopPropagation();

      /* 탭 카운트 증가 */
      tapCount++;
      updateDots(tapCount);

      /* 기존 리셋 타이머 취소 (새 탭이 들어왔으므로 시간을 다시 셈) */
      if (tapResetTimerId) {
        clearTimeout(tapResetTimerId);
        tapResetTimerId = null;
      }

      /* 5회 달성 → 관리자 로그인으로 이동 */
      if (tapCount >= ADMIN_TAP_REQUIRED) {
        tapCount = 0;
        updateDots(0);
        window.location.href = URL_ADMIN_LOGIN;
        return;
      }

      /*
       * ADMIN_TAP_RESET_MS 이내에 다음 탭이 없으면 카운터 리셋.
       * (사용자가 실수로 버튼을 몇 번 눌렀다가 멈춘 경우 초기화)
       */
      tapResetTimerId = setTimeout(function () {
        tapCount = 0;
        updateDots(0);
        tapResetTimerId = null;
      }, ADMIN_TAP_RESET_MS);
    });
  }

})();
