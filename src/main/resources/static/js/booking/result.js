/**
 * static/js/booking/result.js
 * ─────────────────────────────────────────────────────────────────────────
 * UC-07: 예매 완료 확인증 처리
 *
 * ▶ 기능 요약
 *   1. 날짜·시간 포맷 처리 (서버에서 받은 ISO 문자열 → 한국어 표기)
 *   2. 티켓 출력 버튼 → window.print()
 *   3. 포인트 적립 결과 메시지 표시 (UC-07)
 *   4. 처음으로 버튼 → 영화 목록 이동
 *
 * ▶ Controller → View 전달 Model 속성 (result.html)
 *   - paymentDetails  : PaymentDetailsDTO  결제 내역
 *   - reservation     : ReservationDetailsDTO  예매 정보
 *   - earnedPoint     : Integer  이번 예매로 적립된 포인트 (선택)
 *
 * ▶ 연결 template
 *   templates/payment/result.html
 *
 * ▶ 의존
 *   common.js → CineOS.util (날짜 포맷)
 * ─────────────────────────────────────────────────────────────────────────
 */

'use strict';

/* ══════════════════════════════════════════════════════════════════════════
   DOM 참조
══════════════════════════════════════════════════════════════════════════ */

/** 날짜/시간 표시 요소 (data-iso 속성으로 ISO 문자열 제공) */
const $datetimeEls    = document.querySelectorAll('[data-iso]');

/** 포인트 적립 안내 영역 */
const $pointBanner    = document.getElementById('point-earn-banner');
const $earnedPoint    = document.getElementById('earned-point-value');

/** 티켓 출력 버튼 */
const $btnPrint       = document.getElementById('btn-print');

/** 처음으로 버튼 */
const $btnHome        = document.getElementById('btn-go-home');


/* ══════════════════════════════════════════════════════════════════════════
   날짜·시간 포맷
══════════════════════════════════════════════════════════════════════════ */

/**
 * ISO 날짜 문자열을 한국어 형식으로 변환.
 * 예: "2024-06-15T14:30:00" → "2024년 6월 15일 (토) 오후 2:30"
 *
 * @param {string} isoStr - ISO 8601 날짜 문자열
 * @returns {string} 포맷된 문자열
 */
function formatKoreanDatetime(isoStr) {
  if (!isoStr) return '-';

  const date = new Date(isoStr);
  if (isNaN(date.getTime())) return isoStr;

  const DAYS = ['일', '월', '화', '수', '목', '금', '토'];

  const yyyy  = date.getFullYear();
  const month = date.getMonth() + 1;
  const day   = date.getDate();
  const dow   = DAYS[date.getDay()];
  const hours = date.getHours();
  const mins  = String(date.getMinutes()).padStart(2, '0');
  const ampm  = hours < 12 ? '오전' : '오후';
  const h12   = hours % 12 || 12;

  return `${yyyy}년 ${month}월 ${day}일 (${dow}) ${ampm} ${h12}:${mins}`;
}

/**
 * [data-iso] 속성을 가진 모든 요소의 텍스트를 한국어 날짜로 변환.
 */
function formatAllDatetimes() {
  $datetimeEls.forEach((el) => {
    const iso = el.dataset.iso;
    if (iso) {
      el.textContent = formatKoreanDatetime(iso);
    }
  });
}


/* ══════════════════════════════════════════════════════════════════════════
   포인트 적립 안내
══════════════════════════════════════════════════════════════════════════ */

/**
 * 적립 포인트가 있으면 적립 안내 배너 표시.
 * earnedPoint 값은 Thymeleaf가 data-point 속성으로 전달.
 */
function initPointBanner() {
  if (!$pointBanner || !$earnedPoint) return;

  // 서버에서 data-point 속성으로 적립 포인트 전달
  const pointVal = parseInt($pointBanner.dataset.point ?? '0', 10);

  if (pointVal > 0) {
    $earnedPoint.textContent = `${pointVal.toLocaleString('ko-KR')}P`;
    $pointBanner.hidden = false;
  }
}


/* ══════════════════════════════════════════════════════════════════════════
   티켓 출력
══════════════════════════════════════════════════════════════════════════ */

/**
 * 인쇄 버튼 클릭 시 브라우저 인쇄 다이얼로그 호출.
 * 키오스크 환경에서는 영수증 프린터 등과 연결 가능.
 */
function initPrintButton() {
  if (!$btnPrint) return;

  $btnPrint.addEventListener('click', () => {
    window.print();
  });
}


/* ══════════════════════════════════════════════════════════════════════════
   처음으로 버튼
══════════════════════════════════════════════════════════════════════════ */

/**
 * 처음으로 버튼: 영화 목록 페이지로 이동.
 * 키오스크 환경에서 세션 초기화가 필요하면 여기에 추가.
 */
function initHomeButton() {
  if (!$btnHome) return;

  $btnHome.addEventListener('click', () => {
    // 세션 클리어 등 필요 시 추가
    window.location.href = '/movie/list';
  });
}


/* ══════════════════════════════════════════════════════════════════════════
   초기화
══════════════════════════════════════════════════════════════════════════ */

document.addEventListener('DOMContentLoaded', () => {
  formatAllDatetimes();
  initPointBanner();
  initPrintButton();
  initHomeButton();
});
