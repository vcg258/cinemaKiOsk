/**
 * static/js/booking/seat.js
 * ─────────────────────────────────────────────────────────────────────────
 * UC-03: 좌석 선택 + WebSocket 실시간 동기화
 *
 * 담당 기능:
 *   1. 초기 좌석 상태 로드  — GET /api/seats?scheduleId=
 *   2. 좌석 맵 렌더링       — 행·열 기반 그리드 생성
 *   3. 좌석 선택/해제       — REQUIRED_SEAT_COUNT만큼 선택 가능
 *   4. WebSocket 연결       — 실시간 좌석 상태 수신 및 맵 업데이트
 *   5. 연결 실패 fallback   — 30초 간격 폴링으로 좌석 상태 재조회
 *   6. 페이지 이탈 처리     — disconnectWS() + 임시 점유 해제
 *   7. 다음 버튼            — 선택 완료 시 /payment/payment 으로 이동
 *
 * 의존:
 *   - common.js (CineOS 네임스페이스) — 반드시 먼저 로드
 *   - seat.html 인라인 스크립트의 SCHEDULE_ID, THEATER_NO, SEAT_COST,
 *     REQUIRED_SEAT_COUNT, ADULT_COUNT, TEEN_COUNT
 *   - SockJS, @stomp/stompjs CDN (base.html에서 로드됨)
 * ─────────────────────────────────────────────────────────────────────────
 */


/* ────────────────────────────────────────────────────────────────────────
   1. 상수
   ──────────────────────────────────────────────────────────────────────── */

/** WebSocket 서버 엔드포인트 (SockJS) */
const WS_ENDPOINT = '/ws';

/** 좌석 상태 수신 구독 경로 */
const WS_SUBSCRIBE_PATH = `/topic/seat/${SCHEDULE_ID}`;

/** 좌석 선택 발행 경로 */
const WS_SELECT_PATH  = '/app/seat/select';

/** 좌석 해제 발행 경로 */
const WS_RELEASE_PATH = '/app/seat/release';

/**
 * WebSocket 연결 실패 시 폴링 간격 (ms).
 * 30초마다 좌석 상태 재조회.
 */
const POLLING_INTERVAL_MS = 30_000;

/**
 * 좌석 상태 상수.
 * 백엔드 SeatStatusDTO.status 값과 일치해야 함.
 * TODO: 백엔드 ENUM 확정 후 값 재확인.
 */
const SEAT_STATUS = {
  AVAILABLE: 'AVAILABLE', // 선택 가능
  SELECTING: 'SELECTING', // 다른 고객이 임시 점유 중
  TAKEN:     'TAKEN',     // 예매 완료
};


/* ────────────────────────────────────────────────────────────────────────
   2. 상태 (State)
   ──────────────────────────────────────────────────────────────────────── */

/**
 * @type {{
 *   seats:         Array<Object>,   전체 좌석 목록 (SeatStatusDTO 배열)
 *   selectedSeats: Array<string>,   내가 선택한 좌석 ID 목록
 *   stompClient:   Object | null,   STOMP 클라이언트 인스턴스
 *   pollingTimer:  number | null,   폴링 타이머 ID (WS 실패 시 사용)
 *   wsConnected:   boolean,         WebSocket 연결 여부
 * }}
 */
let state = {
  seats:         [],
  selectedSeats: [],
  stompClient:   null,
  pollingTimer:  null,
  wsConnected:   false,
};

/**
 * 상태 부분 갱신.
 * @param {Partial<typeof state>} patch
 */
function setState(patch) {
  state = { ...state, ...patch };
}


/* ────────────────────────────────────────────────────────────────────────
   3. DOM 요소 캐싱
   ──────────────────────────────────────────────────────────────────────── */

let $wsStatus;             // #ws-status
let $seatLoading;          // #seat-loading
let $seatMap;              // #seat-map
let $selectedSeatsDisplay; // #selected-seats-display
let $btnNext;              // #btn-next
let $summaryDatetime;      // #summary-datetime


/* ────────────────────────────────────────────────────────────────────────
   4. 유틸 함수
   ──────────────────────────────────────────────────────────────────────── */

/**
 * ISO datetime 문자열을 "2026.03.20 14:00 ~ 16:30" 형식으로 포맷.
 * @param {string} startStr
 * @param {string} endStr
 * @returns {string}
 */
function formatDatetimeRange(startStr, endStr) {
  if (!startStr) return '-';
  const start = new Date(startStr);
  const end   = endStr ? new Date(endStr) : null;

  const yy  = start.getFullYear();
  const mm  = String(start.getMonth() + 1).padStart(2, '0');
  const dd  = String(start.getDate()).padStart(2, '0');
  const hhs = String(start.getHours()).padStart(2, '0');
  const mms = String(start.getMinutes()).padStart(2, '0');

  let result = `${yy}.${mm}.${dd} ${hhs}:${mms}`;

  if (end) {
    const hhe = String(end.getHours()).padStart(2, '0');
    const mme = String(end.getMinutes()).padStart(2, '0');
    result += ` ~ ${hhe}:${mme}`;
  }
  return result;
}


/* ────────────────────────────────────────────────────────────────────────
   5. API 호출
   ──────────────────────────────────────────────────────────────────────── */

/**
 * 초기 좌석 상태 로드.
 * WebSocket 연결 전 스냅샷을 가져오기 위해 사용.
 * @returns {Promise<Array>} SeatStatusDTO 배열
 */
async function fetchSeats() {
  // TODO: 엔드포인트 백엔드 확정 필요. GET /api/seats?scheduleId=
  return await CineOS.api.get(`/api/seats?scheduleId=${SCHEDULE_ID}`);
}


/* ────────────────────────────────────────────────────────────────────────
   6. 좌석 맵 렌더링
   ──────────────────────────────────────────────────────────────────────── */

/**
 * 좌석 목록을 행·열 기반 그리드로 렌더링.
 * 각 좌석은 <button> 으로 표현.
 * @param {Array<Object>} seats SeatStatusDTO 배열
 *   └ { seatId, row, col, status }
 *   TODO: 백엔드 SeatStatusDTO 필드명 확정 후 수정.
 */
function renderSeatMap(seats) {
  $seatMap.innerHTML = '';

  if (!seats || seats.length === 0) {
    $seatMap.innerHTML = '<p class="seat-empty">좌석 정보를 불러올 수 없습니다.</p>';
    $seatLoading.hidden = true;
    $seatMap.hidden     = false;
    return;
  }

  // 행 기준 그룹화
  const rowMap = {};
  seats.forEach(seat => {
    const row = seat.row ?? 'A'; // TODO: 필드명 확정
    if (!rowMap[row]) rowMap[row] = [];
    rowMap[row].push(seat);
  });

  // 행을 알파벳 오름차순 정렬
  const sortedRows = Object.keys(rowMap).sort();

  sortedRows.forEach(row => {
    // 행 컨테이너
    const rowEl = document.createElement('div');
    rowEl.className = 'seat-row';
    rowEl.setAttribute('aria-label', `${row}열`);

    // 행 라벨 (A, B, C...)
    const rowLabel = document.createElement('span');
    rowLabel.className = 'seat-row__label';
    rowLabel.textContent = row;
    rowLabel.setAttribute('aria-hidden', 'true');
    rowEl.appendChild(rowLabel);

    // 해당 행 좌석 열 오름차순 정렬
    const colSorted = rowMap[row].sort((a, b) => (a.col ?? 0) - (b.col ?? 0));

    colSorted.forEach(seat => {
      const btn = createSeatButton(seat);
      rowEl.appendChild(btn);
    });

    $seatMap.appendChild(rowEl);
  });

  $seatLoading.hidden = true;
  $seatMap.hidden     = false;
}

/**
 * 단일 좌석 버튼 요소 생성.
 * @param {Object} seat SeatStatusDTO
 * @returns {HTMLButtonElement}
 */
function createSeatButton(seat) {
  const seatId  = seat.seatId ?? `${seat.row}${seat.col}`; // TODO: 필드명 확정
  const status  = seat.status ?? SEAT_STATUS.AVAILABLE;
  const isMySelected = state.selectedSeats.includes(String(seatId));

  const btn = document.createElement('button');
  btn.type = 'button';
  btn.className = 'seat';
  btn.dataset.seatId = seatId;
  btn.setAttribute('aria-label', `${seat.row}열 ${seat.col}번 좌석`);

  // 상태별 클래스 및 disabled 처리
  if (isMySelected) {
    btn.classList.add('seat--selected');
    btn.setAttribute('aria-pressed', 'true');
  } else if (status === SEAT_STATUS.AVAILABLE) {
    btn.classList.add('seat--available');
    btn.setAttribute('aria-pressed', 'false');
  } else if (status === SEAT_STATUS.SELECTING) {
    // 다른 고객이 임시 점유 중
    btn.classList.add('seat--selecting');
    btn.disabled = true;
    btn.setAttribute('aria-disabled', 'true');
  } else {
    // 예매 완료
    btn.classList.add('seat--taken');
    btn.disabled = true;
    btn.setAttribute('aria-disabled', 'true');
  }

  // 좌석 번호 표시 (col 숫자)
  btn.textContent = seat.col ?? '';

  if (!btn.disabled) {
    btn.addEventListener('click', () => onSeatClick(seat, btn));
  }

  return btn;
}

/**
 * 특정 좌석 버튼의 상태 클래스를 갱신 (WebSocket 메시지 수신 시 호출).
 *
 * DOM 업데이트와 함께 state.seats 배열의 status도 동기화해야
 * findConsecutiveSeats() 탐색 시 정확한 좌석 상태를 판단할 수 있다.
 *
 * @param {string|number} seatId
 * @param {string}        newStatus SEAT_STATUS 상수값
 */
function updateSeatButton(seatId, newStatus) {
  const btn = $seatMap.querySelector(`[data-seat-id="${seatId}"]`);
  if (!btn) return;

  // 내가 선택한 좌석이면 WS 갱신 무시 (로컬 상태 우선)
  if (state.selectedSeats.includes(String(seatId))) return;

  // ── state.seats 배열에서 해당 좌석의 status도 동기화 ──────────────────
  // DOM만 바꾸면 findConsecutiveSeats()가 여전히 구 status를 참조하는 버그 발생.
  const updatedSeats = state.seats.map(s =>
    String(s.seatId ?? `${s.row}${s.col}`) === String(seatId)
      ? { ...s, status: newStatus }
      : s
  );
  setState({ seats: updatedSeats });

  // ── DOM 상태 클래스 갱신 ──────────────────────────────────────────────
  // 기존 상태 클래스 제거
  btn.classList.remove('seat--available', 'seat--selecting', 'seat--taken');
  btn.disabled = false;
  btn.removeAttribute('aria-disabled');

  if (newStatus === SEAT_STATUS.AVAILABLE) {
    btn.classList.add('seat--available');
    btn.setAttribute('aria-pressed', 'false');
    btn.addEventListener('click', () => {
      // 업데이트된 seats 배열에서 좌석 객체 재탐색
      const seat = state.seats.find(s => String(s.seatId ?? `${s.row}${s.col}`) === String(seatId));
      if (seat) onSeatClick(seat, btn);
    });
  } else if (newStatus === SEAT_STATUS.SELECTING) {
    // 다른 고객이 임시 점유 중 — CSS: .seat--selecting
    btn.classList.add('seat--selecting');
    btn.disabled = true;
    btn.setAttribute('aria-disabled', 'true');
  } else {
    // 예매 완료 — CSS: .seat--taken
    btn.classList.add('seat--taken');
    btn.disabled = true;
    btn.setAttribute('aria-disabled', 'true');
  }
}


/* ────────────────────────────────────────────────────────────────────────
   7. 좌석 선택 이벤트
   ──────────────────────────────────────────────────────────────────────── */

/**
 * 현재 선택된 좌석 전체를 해제하고 UI·WebSocket 상태를 초기화.
 * 2인 이상 연속 선택 모드에서 새로운 위치를 선택할 때 호출.
 */
function releaseAllSelectedSeats() {
  state.selectedSeats.forEach(sid => {
    // 해당 좌석 버튼의 클래스를 선택됨 → 선택 가능으로 복원
    const seatBtn = $seatMap.querySelector(`[data-seat-id="${sid}"]`);
    if (seatBtn) {
      seatBtn.classList.remove('seat--selected');
      seatBtn.classList.add('seat--available');
      seatBtn.setAttribute('aria-pressed', 'false');
    }
    // WebSocket으로 점유 해제 발행
    publishSeatRelease(sid);
  });
  // 상태 초기화
  setState({ selectedSeats: [] });
}

/**
 * 클릭한 좌석을 포함하는 REQUIRED_SEAT_COUNT개의 연속 AVAILABLE 좌석 탐색.
 *
 * 탐색 우선순위:
 *   1. 클릭 좌석이 그룹의 가장 왼쪽(시작)인 경우부터 탐색
 *   2. 오른쪽에 좌석이 부족하면 클릭 좌석을 오른쪽으로 밀면서 재탐색
 *
 * 조건:
 *   - 같은 행(row) 내에서만 탐색
 *   - col 번호가 연속(gap 없음)이어야 함
 *   - 그룹 내 모든 좌석이 AVAILABLE이어야 함 (이미 내가 선택 중인 좌석도 AVAILABLE 취급)
 *
 * @param {Object} clickedSeat - 클릭된 좌석 SeatStatusDTO { seatId, row, col, status }
 * @returns {Array<Object>|null} N개 연속 좌석 배열, 찾지 못하면 null
 */
function findConsecutiveSeats(clickedSeat) {
  const row        = clickedSeat.row;
  const clickedCol = Number(clickedSeat.col);
  const N          = REQUIRED_SEAT_COUNT;

  // 같은 행의 좌석을 col 오름차순으로 정렬
  const rowSeats = state.seats
    .filter(s => s.row === row)
    .sort((a, b) => Number(a.col) - Number(b.col));

  // 클릭한 좌석의 인덱스 확인
  const clickedIdx = rowSeats.findIndex(s => Number(s.col) === clickedCol);
  if (clickedIdx === -1) return null;

  // 그룹 시작 인덱스(startIdx)의 유효 범위 계산
  //   - maxStart: 클릭 좌석이 그룹의 맨 왼쪽(index 0)일 때
  //   - minStart: 클릭 좌석이 그룹의 맨 오른쪽(index N-1)일 때
  const maxStart = Math.min(rowSeats.length - N, clickedIdx);
  const minStart = Math.max(0, clickedIdx - N + 1);

  // 클릭 좌석이 최대한 왼쪽에 오도록 startIdx를 큰 값부터 탐색
  for (let startIdx = maxStart; startIdx >= minStart; startIdx--) {
    const candidate = rowSeats.slice(startIdx, startIdx + N);

    if (candidate.length < N) continue;

    // ① col 연속성 검사: 인접 좌석 col 차이가 정확히 1이어야 함
    const isConsecutive = candidate.every((s, i) =>
      i === 0 || Number(s.col) === Number(candidate[i - 1].col) + 1
    );
    if (!isConsecutive) continue;

    // ② AVAILABLE 여부 검사 (이미 내가 선택한 좌석은 AVAILABLE로 취급)
    const allAvailable = candidate.every(s => {
      const sid = String(s.seatId ?? `${s.row}${s.col}`);
      return s.status === SEAT_STATUS.AVAILABLE || state.selectedSeats.includes(sid);
    });
    if (!allAvailable) continue;

    // 조건 모두 충족: 이 그룹을 반환
    return candidate;
  }

  // 조건을 만족하는 그룹 없음
  return null;
}

/**
 * 좌석 버튼 클릭 핸들러.
 *
 * ▷ 1인 (REQUIRED_SEAT_COUNT === 1): 기존 개별 선택/해제 방식 유지
 * ▷ 2인 이상: 연속 좌석 자동 선택 방식으로 동작
 *   - 클릭한 좌석이 이미 선택된 그룹에 포함되어 있으면 → 전체 해제
 *   - 클릭한 좌석이 AVAILABLE이면 → 이전 선택 해제 후 연속 N개 자동 선택
 *   - 연속 N개 좌석을 찾지 못하면 → 안내 메시지 표시
 *
 * @param {Object}            seat  SeatStatusDTO
 * @param {HTMLButtonElement} btn   클릭된 버튼 요소
 */
function onSeatClick(seat, btn) {
  const seatId = String(seat.seatId ?? `${seat.row}${seat.col}`);

  /* ── 1인: 기존 개별 선택 로직 ──────────────────────────────────────── */
  if (REQUIRED_SEAT_COUNT === 1) {
    if (state.selectedSeats.includes(seatId)) {
      // 좌석 해제
      setState({ selectedSeats: [] });
      btn.classList.remove('seat--selected');
      btn.classList.add('seat--available');
      btn.setAttribute('aria-pressed', 'false');
      publishSeatRelease(seatId);
    } else {
      // 좌석 선택 (이미 1개 선택돼 있으면 먼저 해제)
      releaseAllSelectedSeats();

      setState({ selectedSeats: [seatId] });
      btn.classList.remove('seat--available');
      btn.classList.add('seat--selected');
      btn.setAttribute('aria-pressed', 'true');
      publishSeatSelect(seatId);
    }

    updateSelectionDisplay();
    return;
  }

  /* ── 2인 이상: 연속 좌석 자동 선택 ─────────────────────────────────── */

  // 클릭한 좌석이 현재 선택 그룹에 포함된 경우 → 전체 해제
  if (state.selectedSeats.includes(seatId)) {
    releaseAllSelectedSeats();
    updateSelectionDisplay();
    return;
  }

  // 이전에 선택된 좌석 그룹 전체 해제 (새 위치로 이동)
  releaseAllSelectedSeats();

  // 클릭 좌석을 포함한 N개 연속 AVAILABLE 좌석 탐색
  const consecutive = findConsecutiveSeats(seat);

  if (!consecutive) {
    // 연속 좌석 없음 — 사용자에게 안내
    CineOS.alert.show(
      `이 위치에서 ${REQUIRED_SEAT_COUNT}개의 연속된 빈 좌석을 찾을 수 없습니다.\n다른 좌석을 선택해 주세요.`,
      'warning'
    );
    updateSelectionDisplay();
    return;
  }

  // 찾은 연속 좌석 전체 선택 처리
  const newSeatIds = consecutive.map(s => String(s.seatId ?? `${s.row}${s.col}`));
  setState({ selectedSeats: newSeatIds });

  newSeatIds.forEach(sid => {
    const seatBtn = $seatMap.querySelector(`[data-seat-id="${sid}"]`);
    if (seatBtn) {
      seatBtn.classList.remove('seat--available');
      seatBtn.classList.add('seat--selected');
      seatBtn.setAttribute('aria-pressed', 'true');
    }
    // WebSocket으로 임시 점유 발행
    publishSeatSelect(sid);
  });

  // 하단 선택 현황 및 다음 버튼 갱신
  updateSelectionDisplay();
}

/**
 * 하단 선택된 좌석 표시 및 '다음' 버튼 활성화 여부 갱신.
 */
function updateSelectionDisplay() {
  const count = state.selectedSeats.length;

  // 선택된 좌석 목록 텍스트 (예: "A3, A4")
  $selectedSeatsDisplay.textContent =
    count > 0 ? state.selectedSeats.join(', ') : '없음';

  // 필요한 인원 수만큼 선택됐을 때 버튼 활성화
  const ready = count === REQUIRED_SEAT_COUNT;
  $btnNext.disabled = !ready;
  $btnNext.setAttribute('aria-disabled', String(!ready));

  // 버튼 텍스트에 선택 현황 반영
  $btnNext.textContent = ready
    ? `다음 — 결제 (${count}석)`
    : `좌석을 ${REQUIRED_SEAT_COUNT - count}개 더 선택해주세요`;
}


/* ────────────────────────────────────────────────────────────────────────
   8. WebSocket (STOMP/SockJS)
   ──────────────────────────────────────────────────────────────────────── */

/**
 * WebSocket 연결 및 좌석 topic 구독.
 * SockJS + STOMP 사용. 연결 실패 시 폴링 fallback.
 * @param {number} scheduleId 현재 상영 스케줄 ID
 */
function connectWS(scheduleId) {
  // SockJS 또는 STOMP CDN 미로드 시 폴링 fallback
  if (typeof SockJS === 'undefined' || typeof StompJs === 'undefined') {
    console.warn('[seat.js] WebSocket 라이브러리 미로드. 폴링 fallback 시작.');
    startPolling();
    return;
  }

  try {
    const socket      = new SockJS(WS_ENDPOINT);
    const stompClient = new StompJs.Client({
      webSocketFactory: () => socket,

      // 연결 완료 콜백
      onConnect: () => {
        setState({ stompClient, wsConnected: true });
        hideWsStatus();

        // /topic/seat/{scheduleId} 구독 — 좌석 상태 변경 수신
        stompClient.subscribe(WS_SUBSCRIBE_PATH, (message) => {
          try {
            const data = JSON.parse(message.body);
            // 수신 데이터: { seatId, status } 또는 전체 좌석 배열
            if (Array.isArray(data)) {
              // 전체 좌석 상태 갱신
              setState({ seats: data });
              renderSeatMap(data);
            } else if (data.seatId && data.status) {
              // 단일 좌석 상태 갱신
              updateSeatButton(data.seatId, data.status);
            }
          } catch (e) {
            console.error('[seat.js] WS 메시지 파싱 실패:', e);
          }
        });
      },

      // 연결 오류 콜백
      onStompError: (frame) => {
        console.error('[seat.js] STOMP 오류:', frame);
        setState({ wsConnected: false });
        showWsError();
        startPolling(); // fallback 폴링 시작
      },

      // 재연결 딜레이 (5초)
      reconnectDelay: 5000,
    });

    stompClient.activate();
    setState({ stompClient });

  } catch (err) {
    console.error('[seat.js] WebSocket 연결 실패:', err);
    showWsError();
    startPolling();
  }
}

/**
 * WebSocket 연결 해제.
 * 페이지 이탈(beforeunload) 또는 컴포넌트 언마운트 시 호출.
 */
function disconnectWS() {
  if (state.stompClient && state.wsConnected) {
    // 내가 선택 중인 좌석 전체 해제 발행
    state.selectedSeats.forEach(seatId => publishSeatRelease(seatId));

    state.stompClient.deactivate();
    setState({ stompClient: null, wsConnected: false });
  }

  // 폴링 타이머도 중단
  if (state.pollingTimer) {
    clearInterval(state.pollingTimer);
    setState({ pollingTimer: null });
  }
}

/**
 * 좌석 임시 점유 발행.
 * @param {string} seatId
 */
function publishSeatSelect(seatId) {
  if (!state.stompClient || !state.wsConnected) return;
  state.stompClient.publish({
    destination: WS_SELECT_PATH,
    body: JSON.stringify({ scheduleId: SCHEDULE_ID, seatId }),
  });
}

/**
 * 좌석 점유 해제 발행.
 * @param {string} seatId
 */
function publishSeatRelease(seatId) {
  if (!state.stompClient || !state.wsConnected) return;
  state.stompClient.publish({
    destination: WS_RELEASE_PATH,
    body: JSON.stringify({ scheduleId: SCHEDULE_ID, seatId }),
  });
}

/**
 * WebSocket 연결 실패 시 폴링 fallback 시작.
 * 30초 간격으로 좌석 상태를 REST API로 재조회.
 */
function startPolling() {
  if (state.pollingTimer) return; // 이미 실행 중

  const timer = setInterval(async () => {
    try {
      const seats = await fetchSeats();
      setState({ seats });
      renderSeatMap(seats);
    } catch (err) {
      console.error('[seat.js] 폴링 좌석 조회 실패:', err);
    }
  }, POLLING_INTERVAL_MS);

  setState({ pollingTimer: timer });
}


/* ────────────────────────────────────────────────────────────────────────
   9. WS 상태 UI
   ──────────────────────────────────────────────────────────────────────── */

/**
 * WebSocket 연결 오류 안내 배너 표시.
 */
function showWsError() {
  $wsStatus.hidden = false;
  $wsStatus.className = 'ws-status ws-status--error';
  $wsStatus.innerHTML = `
    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"
         fill="currentColor" width="16" height="16" aria-hidden="true">
      <path d="M1 21h22L12 2 1 21zm12-3h-2v-2h2v2zm0-4h-2v-4h2v4z"/>
    </svg>
    실시간 연결에 실패했습니다. 좌석 정보는 30초마다 자동으로 갱신됩니다.
  `;
}

/**
 * WebSocket 연결 오류 배너 숨김.
 */
function hideWsStatus() {
  $wsStatus.hidden = true;
}


/* ────────────────────────────────────────────────────────────────────────
   10. 다음 버튼 — 결제 페이지로 이동
   ──────────────────────────────────────────────────────────────────────── */

/**
 * '다음' 버튼 클릭 → 결제 페이지(/payment/payment)로 이동.
 * 선택한 좌석 ID 목록, 인원 수, 스케줄 ID를 쿼리 파라미터로 전달.
 * TODO: 실제 구현에서는 세션 또는 POST body로 전달하는 방식으로 변경 권장.
 */
function goToPayment() {
  if (state.selectedSeats.length < REQUIRED_SEAT_COUNT) return;

  const params = new URLSearchParams({
    scheduleId:  SCHEDULE_ID,
    adultCount:  ADULT_COUNT,
    teenCount:   TEEN_COUNT,
    seatIds:     state.selectedSeats.join(','),
  });

  // WebSocket 연결 해제 후 이동
  disconnectWS();

  // TODO: 엔드포인트 백엔드 확정 후 URL 수정
  window.location.href = `/payment/payment?${params.toString()}`;
}


/* ────────────────────────────────────────────────────────────────────────
   11. 초기화 — DOMContentLoaded
   ──────────────────────────────────────────────────────────────────────── */

document.addEventListener('DOMContentLoaded', async () => {

  // ── DOM 요소 참조 할당 ────────────────────────────────────────────────
  $wsStatus             = document.getElementById('ws-status');
  $seatLoading          = document.getElementById('seat-loading');
  $seatMap              = document.getElementById('seat-map');
  $selectedSeatsDisplay = document.getElementById('selected-seats-display');
  $btnNext              = document.getElementById('btn-next');
  $summaryDatetime      = document.getElementById('summary-datetime');

  // ── 날짜·시간 요약 포맷 변환 ──────────────────────────────────────────
  if ($summaryDatetime) {
    const startStr = $summaryDatetime.dataset.start;
    const endStr   = $summaryDatetime.dataset.end;
    $summaryDatetime.textContent = formatDatetimeRange(startStr, endStr);
  }

  // ── 초기 좌석 상태 로드 ───────────────────────────────────────────────
  try {
    const seats = await fetchSeats();
    setState({ seats });
    renderSeatMap(seats);
  } catch (err) {
    console.error('[seat.js] 초기 좌석 로드 실패:', err);
    CineOS.alert.show('좌석 정보를 불러오지 못했습니다. 잠시 후 다시 시도해 주세요.', 'error');
    $seatLoading.hidden = true;
  }

  // ── WebSocket 연결 ────────────────────────────────────────────────────
  connectWS(SCHEDULE_ID);

  // ── 다음 버튼 이벤트 등록 ─────────────────────────────────────────────
  $btnNext.addEventListener('click', goToPayment);

  // ── 초기 선택 현황 표시 갱신 ──────────────────────────────────────────
  updateSelectionDisplay();

  // ── 페이지 이탈 시 WebSocket 해제 + 임시 점유 해제 ───────────────────
  window.addEventListener('beforeunload', () => {
    disconnectWS();
  });
});
