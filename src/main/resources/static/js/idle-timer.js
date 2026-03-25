/**
 * idle-timer.js — 장시간 미조작 방지 타이머
 * ─────────────────────────────────────────────────────────────────────────
 * 예매 관련 페이지에서 사용자 조작이 1분 이상 없으면 홈 화면으로 리다이렉트.
 * base.html 에서 th:if="${idleTimer}" 조건으로 해당 페이지에만 로드됨.
 *
 * ▶ 동작 흐름
 *   1. 페이지 진입 시 1분(60초) 타이머 시작
 *   2. click / touchstart / keydown / mousemove / scroll 감지 → 타이머 1분으로 리셋
 *   3. 남은 시간 15초 이하 → 전체화면 경고 오버레이 표시
 *   4. 0초 도달 → window.location.href = '/' (홈으로 리다이렉트)
 *
 * ▶ 의존 HTML 요소 (base.html 에 th:if="${idleTimer}" 조건으로 삽입)
 *   #idle-badge         : 푸터 카운트다운 뱃지 컨테이너
 *   #idle-countdown     : 뱃지 내부 초 숫자 표시 span
 *   #idle-warning       : 15초 이하 전체화면 경고 오버레이
 *   #idle-warning-count : 오버레이 내부 초 숫자 span
 *
 * ▶ 파일 위치: static/js/idle-timer.js
 */
(function () {
  'use strict';

  /* ── 설정값 ──────────────────────────────────────────────────────── */
  const TOTAL_MS = 60_000;   // 전체 타임아웃: 60초 (1분)
  const WARN_MS  = 15_000;   // 경고 오버레이 표시 임계값: 15초 이하
  const HOME_URL = '/';      // 타임아웃 만료 시 이동할 URL

  /* ── 내부 상태 ───────────────────────────────────────────────────── */
  let remaining = TOTAL_MS;  // 현재 남은 시간 (ms)
  let tickId    = null;      // setInterval 핸들 (clearInterval 용)

  /* ── DOM 참조 (init() 에서 할당) ─────────────────────────────────── */
  let badgeEl;     // #idle-badge         — 푸터 뱃지 전체 컨테이너
  let countEl;     // #idle-countdown     — 뱃지 내부 초 숫자
  let warnEl;      // #idle-warning       — 전체화면 경고 오버레이
  let warnCountEl; // #idle-warning-count — 오버레이 내부 초 숫자

  /* ================================================================
     타이머 리셋
     사용자 상호작용이 감지될 때마다 호출.
     남은 시간을 TOTAL_MS 로 되돌리고 경고 오버레이를 닫는다.
  ================================================================ */
  function resetTimer() {
    remaining = TOTAL_MS;

    /* 경고 오버레이 숨기기 */
    if (warnEl) {
      warnEl.classList.add('idle-warning--hidden');
    }

    /* 뱃지 긴급 상태(빨간색) 해제 */
    if (badgeEl) {
      badgeEl.classList.remove('idle-badge--urgent');
    }

    /* 뱃지 숫자 즉시 갱신 */
    updateUI();
  }

  /* ================================================================
     1초 tick
     setInterval 으로 1초마다 호출됨.
     남은 시간을 감소시키고 UI를 갱신한다.
  ================================================================ */
  function tick() {
    remaining -= 1000;

    /* ── 타임아웃 만료 ────────────────────────────────────────────── */
    if (remaining <= 0) {
      clearInterval(tickId);
      window.location.href = HOME_URL;
      return;
    }

    const sec = Math.ceil(remaining / 1000);

    /* ── 15초 이하 → 경고 오버레이 표시 ──────────────────────────── */
    if (remaining <= WARN_MS) {
      if (warnEl) {
        warnEl.classList.remove('idle-warning--hidden');
        /* 오버레이 내부 카운트 갱신 */
        if (warnCountEl) warnCountEl.textContent = sec;
      }
      /* 뱃지 긴급 색상 적용 */
      if (badgeEl) badgeEl.classList.add('idle-badge--urgent');
    }

    updateUI();
  }

  /* ================================================================
     UI 갱신
     뱃지 내부 카운트다운 숫자를 현재 remaining 값으로 갱신한다.
  ================================================================ */
  function updateUI() {
    if (countEl) {
      countEl.textContent = Math.ceil(remaining / 1000);
    }
  }

  /* ================================================================
     초기화
     DOM 준비 후 이벤트 바인딩 + 타이머 시작.
  ================================================================ */
  function init() {
    /* DOM 요소 할당 */
    badgeEl     = document.getElementById('idle-badge');
    countEl     = document.getElementById('idle-countdown');
    warnEl      = document.getElementById('idle-warning');
    warnCountEl = document.getElementById('idle-warning-count');

    /* ── 사용자 상호작용 이벤트 → 타이머 리셋 ────────────────────────
     * passive: true — scroll / touchstart 이벤트에서 preventDefault 없이
     *                 성능 최적화 (모바일 터치 스크롤 블로킹 방지)
     */
    ['click', 'touchstart', 'keydown', 'mousemove', 'scroll'].forEach(function (evt) {
      document.addEventListener(evt, resetTimer, { passive: true });
    });

    /* 초기 UI 렌더링 후 타이머 시작 */
    updateUI();
    tickId = setInterval(tick, 1000);
  }

  /* DOMContentLoaded 이후 실행 보장 */
  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', init);
  } else {
    init();
  }
})();
