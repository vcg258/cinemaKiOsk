/**
 * static/js/admin/refund.js
 * ─────────────────────────────────────────────────────────────────────────
 * UC-17: 환불 조회 및 처리
 *
 * ▶ 기능 요약
 *   1. 예매번호 또는 휴대폰 번호로 예매 내역 조회
 *      GET /api/admin/refund?reservationId= 또는 ?phone=
 *   2. 조회 결과 표시 (영화명, 좌석, 결제 금액, 결제 시각)
 *   3. 영화 시작 후 10분 초과 시 '환불 처리' 버튼 비활성화 + 안내
 *   4. 환불 확인 → POST /admin/refund/{reservationId}
 *
 * ▶ 에러 메시지 (명세 기준 — 임의 변경 금지)
 *   - 10분 경과: "환불 가능 시간이 초과되었습니다."
 *   - 환불 완료: "환불이 완료되었습니다."
 *
 * ▶ 연결 template
 *   templates/admin/management/refund.html
 *
 * ▶ 의존
 *   common.js → CineOS.api, CineOS.modal, CineOS.alert, CineOS.loading
 * ─────────────────────────────────────────────────────────────────────────
 */

'use strict';

/* ══════════════════════════════════════════════════════════════════════════
   상수
══════════════════════════════════════════════════════════════════════════ */

/** 환불 조회 API */
const REFUND_SEARCH_API = '/api/admin/refund';

/** 환불 처리 API (POST /admin/refund/{reservationId}) */
const REFUND_ACTION_URL = '/admin/refund';

/** 환불 가능 제한 시간 (분) */
const REFUND_LIMIT_MINUTES = 10;


/* ══════════════════════════════════════════════════════════════════════════
   DOM 참조
══════════════════════════════════════════════════════════════════════════ */

const $searchForm        = document.getElementById('refund-search-form');
const $inputReservation  = document.getElementById('input-reservation-id');
const $inputPhone        = document.getElementById('input-phone');
const $btnSearch         = document.getElementById('btn-search');

/** 조회 결과 섹션 (기본 hidden) */
const $resultSection     = document.getElementById('refund-result');
const $resultMovieTitle  = document.getElementById('result-movie-title');
const $resultSeats       = document.getElementById('result-seats');
const $resultAmount      = document.getElementById('result-amount');
const $resultPaidAt      = document.getElementById('result-paid-at');
const $resultWarning     = document.getElementById('result-warning');   // 10분 경과 경고 영역

/** 환불 처리 버튼 및 폼 */
const $refundForm        = document.getElementById('refund-action-form');
const $btnRefund         = document.getElementById('btn-refund');

/** 현재 조회된 예매 ID (환불 처리 시 사용) */
let currentReservationId = null;


/* ══════════════════════════════════════════════════════════════════════════
   예매 조회
══════════════════════════════════════════════════════════════════════════ */

/**
 * 예매번호 또는 휴대폰 번호로 예매 내역 조회.
 * 둘 다 입력된 경우 예매번호 우선.
 */
async function searchReservation() {
  const reservationId = $inputReservation?.value.trim();
  const phone         = $inputPhone?.value.trim();

  // 둘 다 비어 있으면 안내
  if (!reservationId && !phone) {
    CineOS.alert.show('예매번호 또는 휴대폰 번호를 입력해주세요.', 'warning');
    return;
  }

  // 쿼리 파라미터 조합
  const params = reservationId
    ? `reservationId=${encodeURIComponent(reservationId)}`
    : `phone=${encodeURIComponent(phone)}`;

  try {
    CineOS.loading?.show();

    const data = await CineOS.api.get(`${REFUND_SEARCH_API}?${params}`);

    // 조회 결과 화면 표시
    renderResult(data);

  } catch (err) {
    console.error('[refund.js] 예매 조회 실패:', err);

    // 404: 해당 예매 없음
    if (err?.response?.status === 404 || err?.status === 404) {
      CineOS.alert.show('해당 예매 내역을 찾을 수 없습니다.', 'error');
    } else {
      CineOS.alert.show('조회 중 오류가 발생했습니다.', 'error');
    }

    // 결과 영역 숨김
    hideResult();

  } finally {
    CineOS.loading?.hide();
  }
}

/**
 * 조회된 예매 정보를 화면에 렌더링.
 * 영화 시작 후 10분 초과 여부를 확인하여 환불 버튼 활성화 제어.
 *
 * @param {Object} data - PaymentDetailsDTO (reservation, cost, time 등)
 */
function renderResult(data) {
  if (!$resultSection) return;

  // 현재 예매 ID 저장
  currentReservationId = data.id ?? data.reservation?.id ?? null;

  // 영화명
  if ($resultMovieTitle) {
    $resultMovieTitle.textContent =
      data.reservation?.schedule?.movie?.title ?? '-';
  }

  // 좌석 번호 (List<ReservationSeatDTO>)
  if ($resultSeats) {
    const seats = data.reservation?.seats ?? [];
    $resultSeats.textContent =
      seats.length > 0
        ? seats.map(s => s.seatNumber).join(', ')
        : '-';
  }

  // 결제 금액
  if ($resultAmount) {
    const cost = data.cost ?? 0;
    $resultAmount.textContent =
      `${cost.toLocaleString('ko-KR')}원`;
  }

  // 결제 시각
  if ($resultPaidAt) {
    const time = data.time ? new Date(data.time) : null;
    $resultPaidAt.textContent = time
      ? time.toLocaleString('ko-KR')
      : '-';
  }

  // ── 10분 경과 여부 확인 ───────────────────────────────────────────────
  const isExpired = checkRefundExpired(data);

  if ($resultWarning) {
    $resultWarning.hidden = !isExpired;
    if (isExpired) {
      $resultWarning.textContent = '환불 가능 시간이 초과되었습니다.';
    }
  }

  // 환불 버튼 활성화 제어
  if ($btnRefund) {
    $btnRefund.disabled     = isExpired;
    $btnRefund.ariaDisabled = String(isExpired);
  }

  // 환불 폼 action URL 업데이트
  if ($refundForm && currentReservationId) {
    $refundForm.action = `${REFUND_ACTION_URL}/${currentReservationId}`;
  }

  // 결과 섹션 표시
  $resultSection.hidden = false;
}

/**
 * 영화 시작 후 10분 초과 여부 판단.
 * 상영 시작 시각 기준으로 계산.
 *
 * @param {Object} data - PaymentDetailsDTO
 * @returns {boolean} 초과 시 true
 */
function checkRefundExpired(data) {
  const startTimeStr =
    data.reservation?.schedule?.startTime ?? null;

  if (!startTimeStr) return false;

  const startTime = new Date(startTimeStr);
  const now       = new Date();
  const diffMs    = now - startTime;
  const diffMin   = diffMs / 1000 / 60;

  return diffMin > REFUND_LIMIT_MINUTES;
}

/** 결과 섹션 숨김 처리 */
function hideResult() {
  if ($resultSection) $resultSection.hidden = true;
  currentReservationId = null;
}


/* ══════════════════════════════════════════════════════════════════════════
   환불 처리
══════════════════════════════════════════════════════════════════════════ */

/**
 * 환불 확인 모달 → 확인 시 폼 submit.
 * 실제 POST는 폼 submit으로 처리 (Spring Security CSRF 포함).
 */
function confirmRefund() {
  if (!currentReservationId) return;

  CineOS.modal.confirm(
    '정말 환불 처리하시겠습니까? 이 작업은 되돌릴 수 없습니다.',
    () => {
      // 확인 클릭 → 폼 submit
      $refundForm?.submit();
    }
  );
}


/* ══════════════════════════════════════════════════════════════════════════
   이벤트 바인딩
══════════════════════════════════════════════════════════════════════════ */

document.addEventListener('DOMContentLoaded', () => {

  // 조회 폼 submit
  $searchForm?.addEventListener('submit', (e) => {
    e.preventDefault();
    searchReservation();
  });

  // 조회 버튼 클릭
  $btnSearch?.addEventListener('click', searchReservation);

  // 환불 버튼 클릭 → 확인 모달
  $btnRefund?.addEventListener('click', (e) => {
    e.preventDefault();
    confirmRefund();
  });

});
