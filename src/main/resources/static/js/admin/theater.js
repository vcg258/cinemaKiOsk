/**
 * static/js/admin/theater.js
 * ─────────────────────────────────────────────────────────────────────────
 * UC-21: 상영관 정보 수정 + 좌석 정책 편집기
 *
 * ▶ 기능 요약
 *   1. 상영관 선택 드롭다운 변경 시 해당 상영관 정보 로드
 *      GET /api/admin/theater/{no}
 *   2. 진행 중인 예매가 있을 경우 변동 안내 배너 표시
 *   3. 좌석 정책(요금·이름·리클라이너 여부) 수정 폼 처리
 *   4. 수정 완료: POST /admin/management/theater/edit
 *
 * ▶ 연결 template
 *   templates/admin/management/theater/edit.html
 *   templates/admin/management/seat/edit.html
 *
 * ▶ 의존
 *   common.js → CineOS.api, CineOS.alert, CineOS.loading
 * ─────────────────────────────────────────────────────────────────────────
 */

'use strict';

/* ══════════════════════════════════════════════════════════════════════════
   상수
══════════════════════════════════════════════════════════════════════════ */

/** 상영관 단건 조회 API */
const THEATER_API = '/api/admin/theater';


/* ══════════════════════════════════════════════════════════════════════════
   DOM 참조
══════════════════════════════════════════════════════════════════════════ */

/** 상영관 선택 드롭다운 */
const $theaterSelect     = document.getElementById('theater-select');

/** 폼 필드 */
const $fieldPolicyName   = document.getElementById('policy-name');
const $fieldCost         = document.getElementById('seat-cost');
const $fieldCleanupTime  = document.getElementById('cleanup-time');

/** 진행 중 예매 안내 배너 */
const $activeBanner      = document.getElementById('active-reservation-banner');

/** 편집 폼 */
const $theaterForm       = document.getElementById('theater-edit-form');
const $hiddenTheaterNo   = document.getElementById('hidden-theater-no');


/* ══════════════════════════════════════════════════════════════════════════
   상영관 정보 로드
══════════════════════════════════════════════════════════════════════════ */

/**
 * 선택된 상영관의 정보를 API로 가져와 폼에 채워 넣음.
 * 진행 중 예매 여부에 따라 안내 배너 표시.
 *
 * @param {string|number} theaterNo - 상영관 번호
 */
async function loadTheaterInfo(theaterNo) {
  if (!theaterNo) return;

  try {
    CineOS.loading?.show();

    // GET /api/admin/theater/{no}
    const data = await CineOS.api.get(`${THEATER_API}/${theaterNo}`);

    // 폼 필드 채우기
    fillTheaterForm(data);

    // hidden 필드 갱신 (폼 submit 시 상영관 번호 포함)
    if ($hiddenTheaterNo) {
      $hiddenTheaterNo.value = theaterNo;
    }

    // 진행 중 예매 안내 배너
    const hasActive = data.hasActiveReservations ?? false;
    toggleActiveBanner(hasActive);

  } catch (err) {
    console.error('[theater.js] 상영관 정보 로드 실패:', err);
    CineOS.alert.show('상영관 정보를 불러오지 못했습니다.', 'error');
  } finally {
    CineOS.loading?.hide();
  }
}

/**
 * 로드된 TheaterDTO 데이터를 폼 필드에 반영.
 *
 * @param {Object} data - TheaterDTO (no, seatPolicy, cleanupTime)
 */
function fillTheaterForm(data) {
  // 좌석 정책명
  if ($fieldPolicyName && data.seatPolicy?.name !== undefined) {
    $fieldPolicyName.value = data.seatPolicy.name;
  }

  // 좌석 요금
  if ($fieldCost && data.seatPolicy?.cost !== undefined) {
    $fieldCost.value = data.seatPolicy.cost;
  }

  // 정리 시간(분)
  if ($fieldCleanupTime && data.cleanupTime !== undefined) {
    $fieldCleanupTime.value = data.cleanupTime;
  }
}

/**
 * 진행 중 예매 존재 시 변동 안내 배너 표시/숨김.
 *
 * @param {boolean} show
 */
function toggleActiveBanner(show) {
  if (!$activeBanner) return;
  $activeBanner.hidden = !show;
}


/* ══════════════════════════════════════════════════════════════════════════
   좌석 요금 입력 유효성 검증
══════════════════════════════════════════════════════════════════════════ */

/**
 * 좌석 요금 입력값 유효성 검증.
 * 0원 이하 또는 비어 있는 경우 폼 submit 방지.
 *
 * @returns {boolean}
 */
function validateCost() {
  const cost = parseInt($fieldCost?.value ?? '0', 10);
  if (!cost || cost <= 0) {
    CineOS.alert.show('좌석 요금은 0원 초과여야 합니다.', 'warning');
    $fieldCost?.focus();
    return false;
  }
  return true;
}

/**
 * 정리 시간 입력값 유효성 검증.
 * 음수 불가.
 *
 * @returns {boolean}
 */
function validateCleanupTime() {
  const time = parseInt($fieldCleanupTime?.value ?? '0', 10);
  if (time < 0) {
    CineOS.alert.show('정리 시간은 0분 이상이어야 합니다.', 'warning');
    $fieldCleanupTime?.focus();
    return false;
  }
  return true;
}


/* ══════════════════════════════════════════════════════════════════════════
   이벤트 바인딩 및 초기화
══════════════════════════════════════════════════════════════════════════ */

/**
 * 상영관 드롭다운 change 이벤트 바인딩.
 */
function initTheaterSelect() {
  if (!$theaterSelect) return;

  $theaterSelect.addEventListener('change', () => {
    const selectedNo = $theaterSelect.value;
    if (selectedNo) {
      loadTheaterInfo(selectedNo);
    }
  });

  // 페이지 로드 시 이미 선택된 상영관이 있으면 즉시 로드
  if ($theaterSelect.value) {
    loadTheaterInfo($theaterSelect.value);
  }
}

/**
 * 폼 submit 전 유효성 검증 바인딩.
 */
function initFormSubmit() {
  if (!$theaterForm) return;

  $theaterForm.addEventListener('submit', (e) => {
    if (!validateCost() || !validateCleanupTime()) {
      e.preventDefault();
    }
  });
}

document.addEventListener('DOMContentLoaded', () => {
  initTheaterSelect();
  initFormSubmit();
});
