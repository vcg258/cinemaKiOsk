/**
 * static/js/movie/list.js
 * ─────────────────────────────────────────────────────────────────────────
 * UC-01: 상영작 목록 페이지 클라이언트 스크립트
 *
 * 담당 기능:
 *   1. 탭 전환  — 현재 상영 중(now) / 상영 예정(upcoming)
 *   2. 필터 칩  — 장르 / 관람등급 / 상영관 유형 (단일 선택, '전체' 포함)
 *   3. 키워드 검색 — 검색 버튼 클릭 또는 Enter 키
 *   4. API 호출  — GET /api/movies 로 영화 목록 조회
 *   5. 카드 렌더링 — fetchMovies() 결과를 #movie-grid 에 동적 삽입
 *   6. 상태 표시  — 로딩 스피너 / 빈 결과 안내 / 카드 그리드 전환
 *
 * 의존:
 *   common.js (CineOS 네임스페이스) — 반드시 먼저 로드되어야 함.
 *
 * ⚠  API 엔드포인트 및 파라미터명은 백엔드 확정 후 TODO 주석 위치를 수정할 것.
 * ─────────────────────────────────────────────────────────────────────────
 */

/* ────────────────────────────────────────────────────────────────────────
   1. 상수 — 필터 옵션 정의
   각 배열의 { label, value } 에서:
     label : 화면에 표시되는 텍스트
     value : API 요청 파라미터로 전달되는 값 (null = '전체', 필터 미적용)
   ──────────────────────────────────────────────────────────────────────── */

/**
 * 장르 필터 옵션.
 * TODO: 백엔드 genre ENUM 확정 후 value 값 일치시킬 것.
 */
const GENRE_OPTIONS = [
  { label: '전체',       value: null },
  { label: '액션',       value: 'ACTION' },
  { label: '드라마',     value: 'DRAMA' },
  { label: '로맨스',     value: 'ROMANCE' },
  { label: '코미디',     value: 'COMEDY' },
  { label: '공포',       value: 'HORROR' },
  { label: 'SF',         value: 'SF' },
  { label: '애니메이션', value: 'ANIMATION' },
];

/**
 * 관람등급 필터 옵션.
 * MovieDTO.rating ENUM: 'ALL', '12', '15', '19'
 * TODO: 백엔드 DTO 확정 후 value 재확인.
 */
const RATING_OPTIONS = [
  { label: '전체',           value: null },
  { label: '전체관람가',     value: 'ALL' },
  { label: '12세',           value: '12' },
  { label: '15세',           value: '15' },
  { label: '청소년관람불가', value: '19' },
];

/**
 * 상영관 유형 필터 옵션.
 * TODO: 백엔드 theaterType 필드 확정 후 value 수정.
 *       현재 MovieDTO에 theaterType 미포함 → 추가 협의 필요.
 */
const THEATER_OPTIONS = [
  { label: '전체',   value: null },
  { label: '일반관', value: 'STANDARD' },
  { label: 'IMAX',   value: 'IMAX' },
  { label: '4DX',    value: '4DX' },
];

/**
 * 관람등급 → 배지 CSS 클래스 매핑.
 * components.css의 .badge--* 클래스와 일치.
 */
const RATING_BADGE_CLASS = {
  'ALL': 'badge--all',
  '12':  'badge--12',
  '15':  'badge--15',
  '19':  'badge--18',   // components.css는 18 기준이므로 주의
};

/**
 * 관람등급 → 표시 텍스트 매핑.
 */
const RATING_LABEL = {
  'ALL': '전체관람가',
  '12':  '12세',
  '15':  '15세',
  '19':  '청소년불가',
};


/* ────────────────────────────────────────────────────────────────────────
   2. 상태 (State) — 현재 선택된 필터/탭/검색어를 하나의 객체로 관리.
   변경 시 반드시 setState() 를 통해 갱신하고 fetchMovies() 를 재호출.
   ──────────────────────────────────────────────────────────────────────── */

/**
 * @type {{
 *   tab:     'now' | 'upcoming',
 *   genre:   string | null,
 *   rating:  string | null,
 *   theater: string | null,
 *   keyword: string,
 * }}
 */
let state = {
  tab:     'now',    // 현재 상영 중 탭이 기본값
  genre:   null,     // null = '전체' (필터 미적용)
  rating:  null,
  theater: null,
  keyword: '',
};

/**
 * 상태 부분 갱신.
 * @param {Partial<typeof state>} patch 변경할 키-값
 */
function setState(patch) {
  state = { ...state, ...patch };
}


/* ────────────────────────────────────────────────────────────────────────
   3. DOM 요소 캐싱 — DOMContentLoaded 이후 할당.
   ──────────────────────────────────────────────────────────────────────── */

// 탭 버튼
let $tabNow;          // #tab-now
let $tabUpcoming;     // #tab-upcoming

// 필터 칩 그룹 컨테이너
let $filterGenre;     // #filter-genre
let $filterRating;    // #filter-rating
let $filterTheater;   // #filter-theater

// 검색
let $searchInput;     // #search-input
let $searchClearBtn;  // #search-clear-btn
let $searchBtn;       // #search-btn

// 결과 패널
let $gridPanel;       // #movie-grid-panel
let $movieGrid;       // #movie-grid
let $movieLoading;    // #movie-loading
let $movieEmpty;      // #movie-empty
let $movieEmptyText;  // #movie-empty-text


/* ────────────────────────────────────────────────────────────────────────
   4. API 호출
   ──────────────────────────────────────────────────────────────────────── */

/**
 * 영화 목록 API 호출.
 * 현재 state 값을 쿼리 파라미터로 변환하여 요청.
 *
 * TODO: 백엔드와 파라미터명 최종 확인 필요.
 *   - tab  : 'now' | 'upcoming' → ?status=NOW | UPCOMING (예시, 미확정)
 *   - genre, rating, theater : null이면 파라미터 전송하지 않음
 *   - keyword : 공백이면 파라미터 전송하지 않음
 *
 * @returns {Promise<Array>} MovieDTO 배열
 */
async function fetchMovies() {
  // ── 쿼리 파라미터 구성 ─────────────────────────────────────────────
  const params = new URLSearchParams();

  // TODO: 탭 파라미터 이름 백엔드 확정 후 수정 (현재 예시: status=NOW/UPCOMING)
  params.set('status', state.tab === 'now' ? 'NOW' : 'UPCOMING');

  // null 인 필터는 전송하지 않음 (= '전체')
  if (state.genre)   params.set('genre',       state.genre);
  if (state.rating)  params.set('rating',       state.rating);
  if (state.theater) params.set('theaterType',  state.theater); // TODO: 파라미터명 확인

  // 공백 제거 후 빈 문자열이면 전송하지 않음
  const keyword = state.keyword.trim();
  if (keyword) params.set('keyword', keyword);

  // ── API 요청 ────────────────────────────────────────────────────────
  // TODO: 엔드포인트 확정 여부: GET /api/movies
  const url = `/api/movies?${params.toString()}`;
  return await CineOS.api.get(url);
}


/* ────────────────────────────────────────────────────────────────────────
   5. 렌더링 함수
   ──────────────────────────────────────────────────────────────────────── */

/**
 * 필터 칩 버튼 그룹을 동적으로 생성하여 컨테이너에 삽입.
 *
 * @param {HTMLElement} container   칩 버튼을 삽입할 부모 요소
 * @param {Array<{label: string, value: string|null}>} options  필터 옵션 배열
 * @param {string} stateKey         state 객체의 키 이름 ('genre' | 'rating' | 'theater')
 */
function renderFilterGroup(container, options, stateKey) {
  // 기존 내용 비우기
  container.innerHTML = '';

  options.forEach(({ label, value }) => {
    const btn = document.createElement('button');
    btn.type = 'button';
    btn.className = 'filter-chip';
    btn.textContent = label;
    btn.dataset.value = value ?? '';  // null → 빈 문자열로 저장

    // 현재 선택 상태 표시
    if (state[stateKey] === value) {
      btn.classList.add('filter-chip--active');
      btn.setAttribute('aria-pressed', 'true');
    } else {
      btn.setAttribute('aria-pressed', 'false');
    }

    // 클릭 시 상태 갱신 및 재조회
    btn.addEventListener('click', () => {
      setState({ [stateKey]: value });       // 선택한 값으로 갱신
      renderFilterGroup(container, options, stateKey); // 칩 상태 재렌더
      loadMovies();                           // 목록 재조회
    });

    container.appendChild(btn);
  });
}

/**
 * 모든 필터 그룹 렌더링 (초기화 및 상태 변경 후 호출).
 */
function renderAllFilters() {
  renderFilterGroup($filterGenre,   GENRE_OPTIONS,   'genre');
  renderFilterGroup($filterRating,  RATING_OPTIONS,  'rating');
  renderFilterGroup($filterTheater, THEATER_OPTIONS, 'theater');
}

/**
 * 영화 카드 HTML 문자열 생성.
 * components.css의 .card 구조를 따름.
 *
 * @param {Object} movie MovieDTO 객체
 * @param {number}  movie.movieId
 * @param {string}  movie.title
 * @param {string}  movie.genre
 * @param {string}  movie.rating  ('ALL' | '12' | '15' | '19')
 * @param {number}  movie.runtime 상영 시간(분)
 * @param {string}  [movie.posterUrl]  포스터 이미지 URL
 *                                     TODO: 백엔드 MovieDTO에 posterUrl 필드 추가 필요
 * @param {boolean} [movie.soldOut]    매진 여부
 *                                     TODO: 백엔드 MovieDTO에 soldOut 필드 추가 필요
 * @returns {string} <li> HTML 문자열
 */
function createMovieCardHTML(movie) {
  const {
    movieId,
    title      = '제목 없음',
    genre      = '',
    rating     = '',
    runtime    = 0,
    posterUrl  = '/images/placeholder-poster.jpg',  // TODO: 필드명 백엔드 확정
    soldOut    = false,                              // TODO: 필드명 백엔드 확정
  } = movie;

  // 관람등급 배지 클래스 및 텍스트
  const badgeClass = RATING_BADGE_CLASS[rating] || '';
  const ratingText = RATING_LABEL[rating]        || rating;

  // 매진 오버레이 HTML (매진 시에만 렌더)
  const soldOutOverlayHTML = soldOut
    ? `<div class="card__sold-out-overlay">
         <span class="card__sold-out-label">매진</span>
       </div>`
    : '';

  // 런타임: 60분 이상이면 "○시간 ○분" 형식, 미만이면 "○분"
  const runtimeText = runtime > 0
    ? (runtime >= 60
        ? `${Math.floor(runtime / 60)}시간 ${runtime % 60 > 0 ? `${runtime % 60}분` : ''}`
        : `${runtime}분`)
    : '';

  return `
    <li>
      <article
        class="card card--clickable"
        role="button"
        tabindex="0"
        aria-label="${title} 상세 보기"
        data-movie-id="${movieId}"
        onclick="goToDetail(${movieId})"
        onkeydown="handleCardKeyDown(event, ${movieId})"
      >
        <!-- 포스터 이미지 영역 -->
        <div class="card__img-wrap">
          <img
            class="card__img"
            src="${posterUrl}"
            alt="${title} 포스터"
            loading="lazy"
            onerror="this.src='/images/placeholder-poster.jpg'"
          >
          ${soldOutOverlayHTML}
        </div>

        <!-- 카드 정보 영역 -->
        <div class="card__body">
          <h2 class="card__title">${title}</h2>

          <!-- 등급 배지 + 장르 -->
          <div class="card__meta">
            ${badgeClass ? `<span class="badge ${badgeClass}">${ratingText}</span>` : ''}
            <span>${genre}</span>
          </div>

          <!-- 상영 시간 -->
          ${runtimeText ? `<p class="card__runtime">${runtimeText}</p>` : ''}
        </div>
      </article>
    </li>
  `.trim();
}

/**
 * 영화 목록 카드 전체를 #movie-grid 에 렌더링.
 * @param {Array<Object>} movies MovieDTO 배열
 */
function renderCards(movies) {
  $movieGrid.innerHTML = movies.map(createMovieCardHTML).join('');
}

/**
 * 로딩 상태 표시 (카드·빈 결과 숨김).
 */
function showLoading() {
  $movieLoading.hidden = false;
  $movieEmpty.hidden   = true;
  $movieGrid.hidden    = true;
}

/**
 * 빈 결과 상태 표시.
 * @param {string} [msg] 안내 메시지 (미전달 시 기본 메시지)
 */
function showEmpty(msg) {
  // UC-01 대체 흐름: "해당 조건의 상영작이 없습니다."
  $movieEmptyText.textContent = msg || '해당 조건의 상영작이 없습니다.';
  $movieLoading.hidden = true;
  $movieEmpty.hidden   = false;
  $movieGrid.hidden    = true;
}

/**
 * 카드 그리드 표시 (로딩·빈 결과 숨김).
 */
function showGrid() {
  $movieLoading.hidden = true;
  $movieEmpty.hidden   = true;
  $movieGrid.hidden    = false;
}

/**
 * aria-labelledby 를 현재 탭 ID로 갱신 (접근성).
 */
function updateGridLabel() {
  const activeTabId = state.tab === 'now' ? 'tab-now' : 'tab-upcoming';
  $gridPanel.setAttribute('aria-labelledby', activeTabId);
}


/* ────────────────────────────────────────────────────────────────────────
   6. 핵심 플로우 — API 호출 + 결과 렌더링 통합
   ──────────────────────────────────────────────────────────────────────── */

/**
 * 영화 목록 로드.
 * 1. 로딩 표시
 * 2. fetchMovies() 호출
 * 3. 결과에 따라 카드 렌더링 or 빈 결과 표시
 * 4. 에러 시 토스트 알림
 */
async function loadMovies() {
  showLoading();
  updateGridLabel();

  try {
    const movies = await fetchMovies();

    if (!movies || movies.length === 0) {
      // 빈 결과 — 현재 탭에 따라 안내 메시지 구분
      const emptyMsg = state.tab === 'now'
        ? '현재 상영 중인 영화가 없습니다.'
        : '상영 예정 영화가 없습니다.';
      showEmpty(emptyMsg);
    } else {
      renderCards(movies);
      showGrid();
    }

  } catch (err) {
    // 에러 안내: CineOS.alert 토스트 표시 후 빈 결과 상태로 전환
    console.error('[UC-01] 영화 목록 조회 실패:', err);
    CineOS.alert.show(
      err?.message || '영화 목록을 불러오지 못했습니다. 잠시 후 다시 시도해 주세요.',
      'error'
    );
    showEmpty('영화 목록을 불러오지 못했습니다.');
  }
}


/* ────────────────────────────────────────────────────────────────────────
   7. 네비게이션 함수
   ──────────────────────────────────────────────────────────────────────── */

/**
 * 영화 상세 페이지(UC-02)로 이동.
 * @param {number} movieId
 */
function goToDetail(movieId) {
  window.location.href = `/movie/${movieId}`;
}

/**
 * 카드 키보드 접근성 — Enter 또는 Space 키로 상세 이동.
 * @param {KeyboardEvent} event
 * @param {number} movieId
 */
function handleCardKeyDown(event, movieId) {
  if (event.key === 'Enter' || event.key === ' ') {
    event.preventDefault();
    goToDetail(movieId);
  }
}


/* ────────────────────────────────────────────────────────────────────────
   8. 이벤트 핸들러 등록
   ──────────────────────────────────────────────────────────────────────── */

/**
 * 탭 전환 이벤트 등록.
 * 탭 클릭 시 active 클래스 갱신 + 상태 변경 + 목록 재조회.
 */
function initTabs() {
  [$tabNow, $tabUpcoming].forEach((btn) => {
    btn.addEventListener('click', () => {
      const newTab = btn.dataset.tab; // 'now' | 'upcoming'

      // 이미 선택된 탭이면 무시
      if (state.tab === newTab) return;

      // aria 및 active 클래스 갱신
      $tabNow.classList.toggle('movie-tab--active', newTab === 'now');
      $tabNow.setAttribute('aria-selected', String(newTab === 'now'));

      $tabUpcoming.classList.toggle('movie-tab--active', newTab === 'upcoming');
      $tabUpcoming.setAttribute('aria-selected', String(newTab === 'upcoming'));

      // 상태 갱신: 탭 전환 시 필터와 검색어는 유지
      setState({ tab: newTab });
      loadMovies();
    });
  });
}

/**
 * 검색 이벤트 등록.
 *   - 검색 버튼 클릭
 *   - 입력 필드 Enter 키
 *   - 입력값 변화에 따른 X 버튼 표시/숨김
 *   - X 버튼 클릭 시 검색어 초기화
 */
function initSearch() {
  // 입력값 변경: X 버튼 표시 토글
  $searchInput.addEventListener('input', () => {
    $searchClearBtn.hidden = $searchInput.value.trim() === '';
  });

  // Enter 키로 검색 실행
  $searchInput.addEventListener('keydown', (e) => {
    if (e.key === 'Enter') {
      e.preventDefault();
      executeSearch();
    }
  });

  // 검색 버튼 클릭
  $searchBtn.addEventListener('click', executeSearch);

  // X(초기화) 버튼 클릭
  $searchClearBtn.addEventListener('click', () => {
    $searchInput.value = '';
    $searchClearBtn.hidden = true;
    setState({ keyword: '' });
    loadMovies();
    $searchInput.focus(); // 초기화 후 포커스 복귀
  });
}

/**
 * 검색 실행: 현재 입력값을 state에 반영 후 목록 재조회.
 */
function executeSearch() {
  const keyword = $searchInput.value.trim();
  setState({ keyword });
  loadMovies();
}


/* ────────────────────────────────────────────────────────────────────────
   9. 초기화 — DOMContentLoaded
   ──────────────────────────────────────────────────────────────────────── */

document.addEventListener('DOMContentLoaded', () => {

  // ── DOM 요소 참조 할당 ────────────────────────────────────────────────
  $tabNow          = document.getElementById('tab-now');
  $tabUpcoming     = document.getElementById('tab-upcoming');
  $filterGenre     = document.getElementById('filter-genre');
  $filterRating    = document.getElementById('filter-rating');
  $filterTheater   = document.getElementById('filter-theater');
  $searchInput     = document.getElementById('search-input');
  $searchClearBtn  = document.getElementById('search-clear-btn');
  $searchBtn       = document.getElementById('search-btn');
  $gridPanel       = document.getElementById('movie-grid-panel');
  $movieGrid       = document.getElementById('movie-grid');
  $movieLoading    = document.getElementById('movie-loading');
  $movieEmpty      = document.getElementById('movie-empty');
  $movieEmptyText  = document.getElementById('movie-empty-text');

  // ── 이벤트 등록 ───────────────────────────────────────────────────────
  initTabs();
  initSearch();

  // ── 필터 칩 초기 렌더링 ───────────────────────────────────────────────
  renderAllFilters();

  // ── 초기 목록 로드 ────────────────────────────────────────────────────
  loadMovies();
});
