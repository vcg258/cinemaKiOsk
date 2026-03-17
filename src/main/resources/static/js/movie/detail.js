/**
 * static/js/movie/detail.js
 * ─────────────────────────────────────────────────────────────────────────
 * UC-02: 상영작 상세 페이지 클라이언트 스크립트
 *
 * 담당 기능:
 *   1. 러닝타임 포맷 변환 — "109분" → "1시간 49분" 형식으로 표시
 *   2. 예매하기 버튼 클릭 → UC-03 (booking/schedule) 으로 이동
 *
 * 의존:
 *   common.js (CineOS 네임스페이스) — 반드시 먼저 로드되어야 함.
 *   detail.html — #movie-runtime, #btn-book 요소가 있어야 함.
 *
 * 주의:
 *   대부분의 데이터(제목, 장르, 등급, 감독 등)는 Thymeleaf SSR로 렌더링됨.
 *   JS는 SSR로 처리하기 어려운 포맷 변환 및 클라이언트 동작만 담당.
 * ─────────────────────────────────────────────────────────────────────────
 */

document.addEventListener('DOMContentLoaded', () => {

  /* ── 1. 러닝타임 포맷 변환 ─────────────────────────────────────────────
     detail.html의 #movie-runtime 요소에
     data-runtime="109" (분 단위) 속성이 있을 때 "1시간 49분" 형식으로 변환.
     Thymeleaf SSR 폴백으로 이미 "109분"이 텍스트에 있으므로
     JS 로드 실패 시에도 숫자 표시는 유지됨.
  ──────────────────────────────────────────────────────────────────────── */
  const runtimeEl = document.getElementById('movie-runtime');

  if (runtimeEl) {
    const minutes = parseInt(runtimeEl.dataset.runtime, 10);

    if (!isNaN(minutes) && minutes > 0) {
      const hours   = Math.floor(minutes / 60);
      const mins    = minutes % 60;

      // "1시간 49분" or "49분" or "2시간" 형식으로 조합
      let formatted = '';
      if (hours > 0) formatted += `${hours}시간 `;
      if (mins  > 0) formatted += `${mins}분`;

      runtimeEl.textContent = formatted.trim();
    }
  }


  /* ── 2. 예매하기 버튼 클릭 이벤트 ────────────────────────────────────
     #btn-book 버튼에서 data-movie-id 를 읽어
     /booking/schedule?movieId={id} 로 이동.
     disabled 상태(매진)이면 아무 동작도 하지 않음.
  ──────────────────────────────────────────────────────────────────────── */
  const bookBtn = document.getElementById('btn-book');

  if (bookBtn) {
    bookBtn.addEventListener('click', () => {
      // disabled 속성이 있으면 클릭 이벤트 자체가 발생하지 않지만,
      // aria-disabled 처리 등 방어 코드로 한 번 더 확인.
      if (bookBtn.disabled) return;

      const movieId = bookBtn.dataset.movieId;

      if (!movieId) {
        // movieId 가 없는 비정상 상황 → 에러 안내
        CineOS.alert.show('예매 정보를 불러올 수 없습니다. 잠시 후 다시 시도해 주세요.', 'error');
        return;
      }

      // UC-03 날짜·시간 선택 페이지로 이동
      // TODO: 엔드포인트 백엔드 확정 후 URL 수정 (/booking/schedule)
      window.location.href = `/booking/schedule?movieId=${movieId}`;
    });
  }

});
