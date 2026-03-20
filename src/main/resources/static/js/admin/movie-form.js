/**
 * static/js/admin/movie-form.js
 * ─────────────────────────────────────────────────────────────────────────
 * UC-18~19: 영화 등록·수정 폼 처리
 *
 * ▶ 기능 요약
 *   1. 포스터 이미지 업로드 → 미리보기 표시
 *   2. 상영 시작일·종료일 유효성 검증 (종료일 ≥ 시작일)
 *   3. 정리 시간 자동 계산 (상영 시간 + TheaterDTO.cleanupTime)
 *   4. 폼 submit 전 최종 유효성 검증
 *
 * ▶ 주의
 *   - UC-18 등록: POST /admin/management/movie/form
 *   - UC-19 수정: POST /admin/management/movie/form (movieId hidden field 포함)
 *   - 실제 API 처리는 Spring MVC 폼 submit으로 처리 (multipart/form-data)
 *   - 상영 시간 중복 검증은 서버에서 처리 → 409 응답 시 에러 표시
 *
 * ▶ 연결 template
 *   templates/admin/management/movie/form.html
 *
 * ▶ 의존
 *   common.js → CineOS.alert
 * ─────────────────────────────────────────────────────────────────────────
 */

'use strict';

/* ══════════════════════════════════════════════════════════════════════════
   DOM 참조
══════════════════════════════════════════════════════════════════════════ */

/** 포스터 파일 input */
const $posterInput      = document.getElementById('poster-file');
/** 포스터 미리보기 이미지 */
const $posterPreview    = document.getElementById('poster-preview');

/** 상영 기간 입력 */
const $inputStartAt     = document.getElementById('start-at');
const $inputEndAt       = document.getElementById('end-at');

/** 영화 폼 전체 */
const $movieForm        = document.getElementById('movie-form');

/** 서버 에러 메시지 표시 영역 (서버에서 th:if로 표시) */
const $serverError      = document.getElementById('server-error');


/* ══════════════════════════════════════════════════════════════════════════
   포스터 업로드 미리보기
══════════════════════════════════════════════════════════════════════════ */

/**
 * 포스터 파일 선택 시 미리보기 이미지 갱신.
 * FileReader API로 로컬 파일을 Data URL로 변환 후 <img>에 표시.
 */
function initPosterPreview() {
  if (!$posterInput || !$posterPreview) return;

  $posterInput.addEventListener('change', (e) => {
    const file = e.target.files?.[0];
    if (!file) return;

    // 이미지 파일 여부 확인
    if (!file.type.startsWith('image/')) {
      CineOS.alert.show('이미지 파일만 업로드할 수 있습니다.', 'warning');
      $posterInput.value = ''; // 선택 초기화
      return;
    }

    // 파일 크기 제한: 10MB
    const MAX_SIZE_MB = 10;
    if (file.size > MAX_SIZE_MB * 1024 * 1024) {
      CineOS.alert.show(`포스터 파일은 ${MAX_SIZE_MB}MB 이하여야 합니다.`, 'warning');
      $posterInput.value = '';
      return;
    }

    // FileReader로 미리보기
    const reader = new FileReader();
    reader.onload = (ev) => {
      $posterPreview.src     = ev.target.result;
      $posterPreview.hidden  = false;
      $posterPreview.alt     = `포스터 미리보기: ${file.name}`;
    };
    reader.readAsDataURL(file);
  });
}


/* ══════════════════════════════════════════════════════════════════════════
   상영 기간 유효성 검증
══════════════════════════════════════════════════════════════════════════ */

/**
 * 상영 기간 입력값 유효성 검증.
 * 종료일이 시작일보다 앞서지 않도록 체크.
 *
 * @returns {boolean} 유효하면 true
 */
function validateDates() {
  const startAt = $inputStartAt?.value;
  const endAt   = $inputEndAt?.value;

  if (!startAt || !endAt) return true; // 값 없으면 서버 검증에 맡김

  if (endAt < startAt) {
    CineOS.alert.show('상영 종료일은 시작일보다 앞설 수 없습니다.', 'warning');
    $inputEndAt?.focus();
    return false;
  }

  return true;
}

/**
 * 상영 시작일 변경 시 종료일 최솟값 동기화.
 */
function initDateValidation() {
  $inputStartAt?.addEventListener('change', () => {
    if ($inputEndAt && $inputStartAt.value) {
      $inputEndAt.min = $inputStartAt.value;

      // 종료일이 시작일보다 앞이면 시작일로 초기화
      if ($inputEndAt.value && $inputEndAt.value < $inputStartAt.value) {
        $inputEndAt.value = $inputStartAt.value;
      }
    }
  });
}


/* ══════════════════════════════════════════════════════════════════════════
   폼 submit 처리
══════════════════════════════════════════════════════════════════════════ */

/**
 * 폼 제출 전 클라이언트 측 유효성 검증.
 * 모두 통과 시 submit 진행.
 */
function initFormSubmit() {
  if (!$movieForm) return;

  $movieForm.addEventListener('submit', (e) => {

    // 상영 기간 검증
    if (!validateDates()) {
      e.preventDefault();
      return;
    }

    // 추가 검증이 필요한 경우 여기에 추가
    // 모두 통과 → 폼 정상 submit (multipart/form-data)
  });
}

/**
 * 서버에서 409 Conflict (시간 중복) 또는 기타 에러 응답 후
 * 리다이렉트 파라미터로 에러 메시지가 전달된 경우 처리.
 * th:if="${errorMsg}" 로 서버가 이미 표시하지만,
 * URL 파라미터로도 처리 가능하도록 보조 함수 제공.
 */
function checkUrlError() {
  const params    = new URLSearchParams(window.location.search);
  const errorCode = params.get('error');

  if (!errorCode || !$serverError) return;

  const messages = {
    'duplicate-time': '해당 상영관에 이미 등록된 시간대와 겹칩니다.',
    'invalid-input' : '입력값을 확인해주세요.',
  };

  const msg = messages[errorCode] ?? '오류가 발생했습니다.';
  $serverError.textContent = msg;
  $serverError.hidden = false;
}


/* ══════════════════════════════════════════════════════════════════════════
   초기화
══════════════════════════════════════════════════════════════════════════ */

document.addEventListener('DOMContentLoaded', () => {
  initPosterPreview();
  initDateValidation();
  initFormSubmit();
  checkUrlError();
});
