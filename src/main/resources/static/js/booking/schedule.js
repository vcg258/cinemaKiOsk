/**
 * static/js/booking/schedule.js
 * ─────────────────────────────────────────────────────────────────────────
 * UC-03: 날짜 · 시간 · 인원 선택 페이지 클라이언트 스크립트
 *
 * 담당 기능:
 *   1. 날짜 버튼 렌더링  — SCHEDULES_DATA에서 날짜만 추출·중복 제거
 *   2. 시간 카드 렌더링  — 선택된 날짜에 해당하는 스케줄 목록
 *   3. 인원 스피너       — 성인/청소년 증감, 최소 합계 1명, 최대 8명
 *   4. 금액 계산         — THEATER_COST * 인원 수 실시간 합산
 *   5. 다음 버튼 활성화  — 날짜 + 시간 선택 완료 시
 *   6. 좌석 선택 이동    — /booking/seat?scheduleId=&adultCount=&teenCount=
 *
 * 의존:
 *   - common.js (CineOS 네임스페이스) — 반드시 먼저 로드
 *   - schedule.html 인라인 스크립트의 SCHEDULES_DATA, MOVIE_DATA, THEATER_COST
 * ─────────────────────────────────────────────────────────────────────────
 */


/* ────────────────────────────────────────────────────────────────────────
   1. 상수
   ──────────────────────────────────────────────────────────────────────── */

/** 인원 선택 최대 합계 */
const MAX_TOTAL_PEOPLE = 8;

/**
 * 청소년 요금 비율 (성인 요금 대비 0.8 = 80%).
 * TODO: 백엔드 DiscountPolicy 확정 후 서버에서 주입받도록 교체.
 */
const TEEN_COST_RATIO = 0.8;


/* ────────────────────────────────────────────────────────────────────────
   2. 상태 (State)
   ──────────────────────────────────────────────────────────────────────── */

/**
 * @type {{
 *   selectedDate:     string | null,  선택된 날짜 (예: "2026-03-20")
 *   selectedSchedule: Object | null,  선택된 ScheduleDTO 객체
 *   adultCount:       number,         성인 인원 수
 *   teenCount:        number,         청소년 인원 수
 * }}
 */
let state = {
  selectedDate:     null,
  selectedSchedule: null,
  adultCount:       1,
  teenCount:        0,
};

/**
 * 상태 부분 갱신.
 * @param {Partial<typeof state>} patch
 */
function setState(patch) {
  state = { ...state, ...patch };
}


/* ────────────────────────────────────────────────────────────────────────
   3. DOM 요소 캐싱 (DOMContentLoaded 이후 할당)
   ──────────────────────────────────────────────────────────────────────── */

let $runtimeEl;      // #schedule-runtime
let $dateBtnGroup;   // #date-btn-group
let $timeSection;    // #time-section
let $timeCardGroup;  // #time-card-group
let $peopleSection;  // #people-section
let $adultMinus;     // #adult-minus
let $adultPlus;      // #adult-plus
let $adultCount;     // #adult-count
let $adultPrice;     // #adult-price
let $teenMinus;      // #teen-minus
let $teenPlus;       // #teen-plus
let $teenCount;      // #teen-count
let $teenPrice;      // #teen-price
let $totalPrice;     // #total-price
let $btnNext;        // #btn-next


/* ────────────────────────────────────────────────────────────────────────
   4. 유틸 함수
   ──────────────────────────────────────────────────────────────────────── */

/**
 * ISO datetime 문자열 (예: "2026-03-20T14:00:00")에서 날짜 부분만 반환.
 * @param {string} isoStr
 * @returns {string} "2026-03-20"
 */
function toDateStr(isoStr) {
  return isoStr.split('T')[0];
}

/**
 * ISO datetime 문자열에서 "HH:MM" 형식의 시간 반환.
 * @param {string} isoStr
 * @returns {string} "14:00"
 */
function toTimeStr(isoStr) {
  return isoStr.split('T')[1]?.slice(0, 5) ?? '--:--';
}

/**
 * "YYYY-MM-DD" 날짜 문자열을 "MM/DD (요일)" 형식으로 포맷.
 * @param {string} dateStr
 * @returns {string} "03/20 (금)"
 */
function formatDateLabel(dateStr) {
  const date = new Date(dateStr + 'T00:00:00');
  const mm   = String(date.getMonth() + 1).padStart(2, '0');
  const dd   = String(date.getDate()).padStart(2, '0');
  const days = ['일', '월', '화', '수', '목', '금', '토'];
  const dow  = days[date.getDay()];
  return `${mm}/${dd} (${dow})`;
}

/**
 * 주어진 상영 시작 시간이 이미 지난 상영인지 확인.
 * 현재 시각 기준 5분 경과한 상영은 선택 불가.
 * @param {string} startTimeStr ISO datetime 문자열
 * @returns {boolean} true = 이미 지난 상영
 */
function isPastSchedule(startTimeStr) {
  const startTime = new Date(startTimeStr);
  const cutoff    = new Date(Date.now() - 5 * 60 * 1000); // 현재 - 5분
  return startTime < cutoff;
}

/**
 * 러닝타임(분)을 "1시간 49분" 형식으로 변환.
 * @param {number} minutes
 * @returns {string}
 */
function formatRuntime(minutes) {
  if (!minutes || minutes <= 0) return '';
  const h = Math.floor(minutes / 60);
  const m = minutes % 60;
  let result = '';
  if (h > 0) result += `${h}시간 `;
  if (m > 0) result += `${m}분`;
  return result.trim();
}

/**
 * 금액을 "15,000원" 형식으로 포맷.
 * @param {number} amount
 * @returns {string}
 */
function formatCurrencyLocal(amount) {
  return `${Number(amount).toLocaleString('ko-KR')}원`;
}


/* ────────────────────────────────────────────────────────────────────────
   5. 렌더링 함수
   ──────────────────────────────────────────────────────────────────────── */

/**
 * SCHEDULES_DATA에서 날짜를 중복 없이 추출하여 날짜 버튼 그룹을 렌더링.
 * 과거 날짜는 버튼을 disabled로 표시.
 */
function renderDates() {
  $dateBtnGroup.innerHTML = '';

  if (!SCHEDULES_DATA || SCHEDULES_DATA.length === 0) {
    $dateBtnGroup.innerHTML =
      '<p class="schedule-empty">현재 예매 가능한 날짜가 없습니다.</p>';
    return;
  }

  // 날짜만 추출 후 중복 제거 및 오름차순 정렬
  const uniqueDates = [...new Set(
    SCHEDULES_DATA.map(s => toDateStr(s.startTime))
  )].sort();

  const todayStr = new Date().toISOString().split('T')[0]; // "2026-03-20"

  uniqueDates.forEach(dateStr => {
    const btn = document.createElement('button');
    btn.type = 'button';
    btn.className = 'date-btn';
    btn.dataset.date = dateStr;
    btn.textContent = formatDateLabel(dateStr);

    // 오늘 날짜 강조 클래스
    if (dateStr === todayStr) {
      btn.classList.add('date-btn--today');
    }

    // 현재 선택된 날짜 활성 클래스
    if (state.selectedDate === dateStr) {
      btn.classList.add('date-btn--active');
      btn.setAttribute('aria-pressed', 'true');
    } else {
      btn.setAttribute('aria-pressed', 'false');
    }

    btn.addEventListener('click', () => onDateSelect(dateStr));
    $dateBtnGroup.appendChild(btn);
  });
}

/**
 * 선택된 날짜에 해당하는 상영 시간 카드 목록을 렌더링.
 * 이미 지난 상영 시간은 disabled + '종료' 표시.
 * @param {string} dateStr 선택된 날짜 "YYYY-MM-DD"
 */
function renderTimeSlots(dateStr) {
  $timeCardGroup.innerHTML = '';

  // 선택된 날짜에 해당하는 스케줄 필터링
  const slots = SCHEDULES_DATA.filter(s => toDateStr(s.startTime) === dateStr);

  if (slots.length === 0) {
    $timeCardGroup.innerHTML =
      '<p class="schedule-empty">선택한 날짜의 상영 시간이 없습니다.</p>';
    return;
  }

  // 시작 시간 오름차순 정렬
  slots.sort((a, b) => new Date(a.startTime) - new Date(b.startTime));

  slots.forEach(schedule => {
    const past = isPastSchedule(schedule.startTime);

    const btn = document.createElement('button');
    btn.type = 'button';
    btn.className = 'time-card';
    btn.dataset.scheduleId = schedule.id;

    if (past) {
      btn.disabled = true;
      btn.classList.add('time-card--past');
      btn.setAttribute('aria-disabled', 'true');
    }

    // 현재 선택된 스케줄 활성 클래스
    if (state.selectedSchedule?.id === schedule.id) {
      btn.classList.add('time-card--active');
      btn.setAttribute('aria-pressed', 'true');
    } else {
      btn.setAttribute('aria-pressed', 'false');
    }

    // 카드 내용: 시작 시간 / 종료 시간 / 상영관 번호
    btn.innerHTML = `
      <span class="time-card__time">${toTimeStr(schedule.startTime)}</span>
      <span class="time-card__end">~ ${toTimeStr(schedule.endTime)}</span>
      <span class="time-card__theater">${schedule.no}관</span>
      ${past ? '<span class="time-card__past-label">종료</span>' : ''}
    `;

    if (!past) {
      btn.addEventListener('click', () => onTimeSelect(schedule));
    }

    $timeCardGroup.appendChild(btn);
  });
}

/**
 * 인원 스피너 UI 및 금액 표시를 현재 state 기준으로 갱신.
 */
function updatePeopleUI() {
  const adultCost = THEATER_COST;
  const teenCost  = Math.round(THEATER_COST * TEEN_COST_RATIO);
  const totalAmt  = adultCost * state.adultCount + teenCost * state.teenCount;
  const totalPpl  = state.adultCount + state.teenCount;

  // 카운트 표시 갱신
  $adultCount.textContent = state.adultCount;
  $teenCount.textContent  = state.teenCount;

  // 요금 표시 갱신
  $adultPrice.textContent = formatCurrencyLocal(adultCost);
  $teenPrice.textContent  = formatCurrencyLocal(teenCost);
  $totalPrice.textContent = formatCurrencyLocal(totalAmt);

  // 감소 버튼: 합계가 1명 이하로 내려가지 않도록
  $adultMinus.disabled = state.adultCount <= 0 || (totalPpl <= 1);
  $teenMinus.disabled  = state.teenCount  <= 0 || (totalPpl <= 1 && state.teenCount <= 1);

  // 증가 버튼: 합계 최대 초과 시 비활성
  $adultPlus.disabled = totalPpl >= MAX_TOTAL_PEOPLE;
  $teenPlus.disabled  = totalPpl >= MAX_TOTAL_PEOPLE;
}

/**
 * '다음' 버튼 활성화 여부를 현재 상태에 맞게 갱신.
 * 날짜 + 시간이 모두 선택된 경우에만 활성화.
 */
function updateNextBtn() {
  const ready = state.selectedDate !== null && state.selectedSchedule !== null;
  $btnNext.disabled = !ready;
  $btnNext.setAttribute('aria-disabled', String(!ready));
}


/* ────────────────────────────────────────────────────────────────────────
   6. 이벤트 핸들러
   ──────────────────────────────────────────────────────────────────────── */

/**
 * 날짜 버튼 클릭 핸들러.
 * 선택된 날짜를 상태에 반영하고, 시간 섹션 표시 및 시간 카드 렌더링.
 * @param {string} dateStr "YYYY-MM-DD"
 */
function onDateSelect(dateStr) {
  setState({ selectedDate: dateStr, selectedSchedule: null });

  // 날짜 버튼 active 클래스 갱신
  $dateBtnGroup.querySelectorAll('.date-btn').forEach(btn => {
    const isSelected = btn.dataset.date === dateStr;
    btn.classList.toggle('date-btn--active', isSelected);
    btn.setAttribute('aria-pressed', String(isSelected));
  });

  // 시간 섹션 표시 + 렌더링
  $timeSection.hidden = false;
  renderTimeSlots(dateStr);

  // 인원 섹션 숨김 (날짜 재선택 시 초기화)
  $peopleSection.hidden = true;

  // 다음 버튼 갱신
  updateNextBtn();

  // 시간 선택 섹션으로 스크롤
  $timeSection.scrollIntoView({ behavior: 'smooth', block: 'start' });
}

/**
 * 시간 카드 클릭 핸들러.
 * 선택된 스케줄을 상태에 반영하고, 인원 섹션 표시.
 * @param {Object} schedule ScheduleDTO
 */
function onTimeSelect(schedule) {
  setState({ selectedSchedule: schedule });

  // 시간 카드 active 클래스 갱신
  $timeCardGroup.querySelectorAll('.time-card').forEach(btn => {
    const isSelected = Number(btn.dataset.scheduleId) === schedule.id;
    btn.classList.toggle('time-card--active', isSelected);
    btn.setAttribute('aria-pressed', String(isSelected));
  });

  // 인원 섹션 표시 + UI 갱신
  $peopleSection.hidden = false;
  updatePeopleUI();
  updateNextBtn();

  // 인원 섹션으로 스크롤 (키오스크 터치 UX 개선)
  $peopleSection.scrollIntoView({ behavior: 'smooth', block: 'start' });
}

/**
 * 인원 스피너 이벤트 등록.
 */
function initPeopleSpinner() {

  // 성인 감소
  $adultMinus.addEventListener('click', () => {
    if (state.adultCount <= 0) return;
    if (state.adultCount + state.teenCount <= 1) return; // 합계 1명 최소 유지
    setState({ adultCount: state.adultCount - 1 });
    updatePeopleUI();
  });

  // 성인 증가
  $adultPlus.addEventListener('click', () => {
    if (state.adultCount + state.teenCount >= MAX_TOTAL_PEOPLE) return;
    setState({ adultCount: state.adultCount + 1 });
    updatePeopleUI();
  });

  // 청소년 감소
  $teenMinus.addEventListener('click', () => {
    if (state.teenCount <= 0) return;
    if (state.adultCount + state.teenCount <= 1) return; // 합계 1명 최소 유지
    setState({ teenCount: state.teenCount - 1 });
    updatePeopleUI();
  });

  // 청소년 증가
  $teenPlus.addEventListener('click', () => {
    if (state.adultCount + state.teenCount >= MAX_TOTAL_PEOPLE) return;
    setState({ teenCount: state.teenCount + 1 });
    updatePeopleUI();
  });
}

/**
 * '다음' 버튼 클릭 → 좌석 선택 페이지로 이동.
 * scheduleId, adultCount, teenCount 를 쿼리 파라미터로 전달.
 */
function goToSeat() {
  if (!state.selectedSchedule) return;

  const params = new URLSearchParams({
    scheduleId: state.selectedSchedule.id,
    adultCount: state.adultCount,
    teenCount:  state.teenCount,
  });

  // TODO: 엔드포인트 백엔드 확정 후 URL 수정
  window.location.href = `/booking/seat?${params.toString()}`;
}


/* ────────────────────────────────────────────────────────────────────────
   7. 초기화 — DOMContentLoaded
   ──────────────────────────────────────────────────────────────────────── */

document.addEventListener('DOMContentLoaded', () => {

  // ── DOM 요소 참조 할당 ────────────────────────────────────────────────
  $runtimeEl     = document.getElementById('schedule-runtime');
  $dateBtnGroup  = document.getElementById('date-btn-group');
  $timeSection   = document.getElementById('time-section');
  $timeCardGroup = document.getElementById('time-card-group');
  $peopleSection = document.getElementById('people-section');
  $adultMinus    = document.getElementById('adult-minus');
  $adultPlus     = document.getElementById('adult-plus');
  $adultCount    = document.getElementById('adult-count');
  $adultPrice    = document.getElementById('adult-price');
  $teenMinus     = document.getElementById('teen-minus');
  $teenPlus      = document.getElementById('teen-plus');
  $teenCount     = document.getElementById('teen-count');
  $teenPrice     = document.getElementById('teen-price');
  $totalPrice    = document.getElementById('total-price');
  $btnNext       = document.getElementById('btn-next');

  // ── 러닝타임 포맷 변환 (SSR "109분" → JS "1시간 49분") ────────────────
  if ($runtimeEl) {
    const minutes   = parseInt($runtimeEl.dataset.runtime, 10);
    const formatted = formatRuntime(minutes);
    if (formatted) $runtimeEl.textContent = formatted;
  }

  // ── 날짜 버튼 초기 렌더링 ─────────────────────────────────────────────
  renderDates();

  // ── 인원 스피너 이벤트 등록 ───────────────────────────────────────────
  initPeopleSpinner();

  // ── 다음 버튼 이벤트 등록 ─────────────────────────────────────────────
  $btnNext.addEventListener('click', goToSeat);
});
