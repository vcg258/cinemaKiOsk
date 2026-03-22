/**
 * static/js/admin/stats.js
 * ─────────────────────────────────────────────────────────────────────────
 * UC-12~16 통계 차트 공통 모듈 (Chart.js 4.x)
 *
 * ▶ 의존
 *   - admin-base.html 에서 Chart.js CDN 로드 완료 후 이 파일 실행됨
 *   - common.js → CineOS.api, CineOS.alert, CineOS.loading
 *
 * ▶ 각 통계 HTML 에서 반드시 이 파일보다 먼저 선언할 변수
 *   const STAT_TYPE = 'daily';               // 'daily'|'monthly'|'by-day'|'by-hour'|'by-movie'
 *   const STAT_DATA = [[${statistics}]];     // Thymeleaf SSR 초기 데이터 (없으면 [])
 *   (by-movie 전용) const SELECTED_MOVIE_ID = [[${selectedMovie?.movieId}]];
 *
 * ▶ HTML element ID 규약 (각 통계 html 과 1:1 매핑)
 *   #startDate    날짜 시작 input[type=date]
 *   #endDate      날짜 종료 input[type=date]
 *   #statsChart   Chart.js 렌더링 대상 <canvas>
 *   #statsTable   데이터 테이블 <table>
 *   #movieSelect  영화 선택 <select> (by-movie 전용)
 *
 * ▶ 전역 함수 (HTML onclick 에서 직접 호출)
 *   fetchAndRenderStats(startDate, endDate, movieId?)
 * ─────────────────────────────────────────────────────────────────────────
 */

'use strict';

/* ══════════════════════════════════════════════════════════════════════════
   1. DOM 참조 — 각 통계 HTML 의 element ID 와 일치
══════════════════════════════════════════════════════════════════════════ */

/** 날짜 시작 input (by-movie 에는 없음) */
const $startDate = document.getElementById('startDate');

/** 날짜 종료 input (by-movie 에는 없음) */
const $endDate = document.getElementById('endDate');

/** Chart.js 렌더링 대상 <canvas> */
const $chartCanvas = document.getElementById('statsChart');

/** 현재 Chart.js 인스턴스 — 재조회 시 destroy 후 재생성 */
let chartInstance = null;


/* ══════════════════════════════════════════════════════════════════════════
   2. 날짜 유틸
══════════════════════════════════════════════════════════════════════════ */

/**
 * 오늘 날짜를 'YYYY-MM-DD' 문자열로 반환.
 * @returns {string}
 */
function todayISO() {
  const d  = new Date();
  const mm = String(d.getMonth() + 1).padStart(2, '0');
  const dd = String(d.getDate()).padStart(2, '0');
  return `${d.getFullYear()}-${mm}-${dd}`;
}

/**
 * ISO 날짜 문자열에서 n일 뺀 날짜를 'YYYY-MM-DD' 로 반환.
 * @param {string} iso - 기준 날짜 ('YYYY-MM-DD')
 * @param {number} n   - 뺄 일수
 * @returns {string}
 */
function subtractDays(iso, n) {
  const d  = new Date(iso);
  d.setDate(d.getDate() - n);
  const mm = String(d.getMonth() + 1).padStart(2, '0');
  const dd = String(d.getDate()).padStart(2, '0');
  return `${d.getFullYear()}-${mm}-${dd}`;
}


/* ══════════════════════════════════════════════════════════════════════════
   3. 날짜 필터 초기화
══════════════════════════════════════════════════════════════════════════ */

/**
 * 날짜 입력 필드 초기값 설정 및 미래 날짜 선택 방지.
 *
 * - by-movie 는 날짜 필터 없으므로 바로 반환.
 * - 기본 범위: 오늘 기준 최근 7일 (오늘 포함).
 * - input[max] = 오늘로 고정 → 브라우저 UI 에서 미래 날짜 선택 불가.
 */
function initDateFilter() {
  const type = typeof STAT_TYPE !== 'undefined' ? STAT_TYPE : 'daily';

  /* by-movie 는 날짜 필터가 없으므로 스킵 */
  if (type === 'by-movie') return;

  const today = todayISO();

  if ($startDate) {
    $startDate.max = today;
    /* Thymeleaf 에서 내려온 값 없을 때만 기본값(7일 전) 세팅 */
    if (!$startDate.value) $startDate.value = subtractDays(today, 6);
  }

  if ($endDate) {
    $endDate.max = today;
    if (!$endDate.value) $endDate.value = today;
  }

  /* 시작일 변경 → 종료일 min 갱신 (날짜 역전 방지) */
  $startDate?.addEventListener('change', function () {
    if (!$endDate) return;
    $endDate.min = $startDate.value;
    /* 종료일이 시작일보다 앞서면 시작일로 맞춤 */
    if ($endDate.value < $startDate.value) {
      $endDate.value = $startDate.value;
    }
  });
}


/* ══════════════════════════════════════════════════════════════════════════
   4. 데이터 → Chart.js labels / datasets 변환
══════════════════════════════════════════════════════════════════════════ */

/** 영어 요일 키 → 한글 1자 변환 테이블 */
const DAY_KO = {
  SUN: '일', MON: '월', TUE: '화',
  WED: '수', THU: '목', FRI: '금', SAT: '토',
};

/**
 * StatisticsDTO 배열을 Chart.js 에 넘길 형태로 변환.
 *
 * 타입별 레이블 전략:
 *   by-day   → 요일 합산 후 일~토 순 정렬 (SUN→일 … SAT→토)
 *   by-hour  → stat.hour + '시' (백엔드에서 hour 필드로 내려올 예정)
 *   daily    → stat.date 또는 stat.scheduleId (연결 전 플레이스홀더)
 *   monthly  → stat.date 또는 stat.scheduleId
 *   by-movie → stat.date 또는 stat.scheduleId
 *
 * @param {Array<Object>} data - StatisticsDTO 배열
 * @returns {{ labels: string[], revenues: number[], counts: number[] }}
 */
function buildChartData(data) {
  const type = typeof STAT_TYPE !== 'undefined' ? STAT_TYPE : 'daily';

  /* ── by-day: 요일별 집계 후 일~토 정렬 ── */
  if (type === 'by-day') {
    const ORDER = ['SUN', 'MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT'];
    const map   = {};

    /* 같은 요일이 여러 행 있을 수 있으므로 합산 */
    data.forEach(function (d) {
      const key = d.day;
      if (!key) return;
      if (!map[key]) map[key] = { revenue: 0, customerCount: 0 };
      map[key].revenue       += (d.revenue       ?? 0);
      map[key].customerCount += (d.customerCount ?? 0);
    });

    /* 데이터가 있는 요일만 ORDER 순으로 추출 */
    const keys = ORDER.filter(function (k) { return !!map[k]; });
    return {
      labels:   keys.map(function (k) { return DAY_KO[k] ?? k; }),
      revenues: keys.map(function (k) { return map[k].revenue; }),
      counts:   keys.map(function (k) { return map[k].customerCount; }),
    };
  }

  /* ── 나머지 타입: 행 순서 그대로 ── */

  /*
   * 레이블 우선순위:
   *   1. stat.label      백엔드가 직접 내려준 표시용 문자열
   *   2. stat.date       날짜 문자열 ('YYYY-MM-DD' 또는 'YYYY-MM')
   *   3. stat.hour       시간대 숫자 → '9시' 형태 (by-hour 전용)
   *   4. stat.day        요일 영어 키 → 한글
   *   5. stat.scheduleId 최후 fallback (백엔드 연결 전 플레이스홀더)
   */
  const labels = data.map(function (d) {
    if (d.label != null) return String(d.label);
    if (d.date  != null) return String(d.date);
    if (d.hour  != null) return d.hour + '시';
    if (d.day   != null) return DAY_KO[d.day] ?? d.day;
    return String(d.scheduleId ?? '-');
  });

  return {
    labels,
    revenues: data.map(function (d) { return d.revenue       ?? 0; }),
    counts:   data.map(function (d) { return d.customerCount ?? 0; }),
  };
}


/* ══════════════════════════════════════════════════════════════════════════
   5. Chart.js 렌더링
══════════════════════════════════════════════════════════════════════════ */

/**
 * Chart.js 혼합 차트(막대 + 꺾은선) 렌더링.
 * 기존 차트가 있으면 destroy 후 재생성 (메모리 누수 방지).
 *
 * @param {Array<Object>} data - StatisticsDTO 배열
 */
function renderChart(data) {
  if (!$chartCanvas)               return; /* 캔버스 없음 */
  if (!data || data.length === 0)  return; /* 데이터 없음 */

  /* 기존 인스턴스 정리 */
  if (chartInstance) {
    chartInstance.destroy();
    chartInstance = null;
  }

  const { labels, revenues, counts } = buildChartData(data);

  chartInstance = new Chart($chartCanvas, {
    type: 'bar',
    data: {
      labels,
      datasets: [
        /* ── 수익 — 막대 (왼쪽 Y축) ── */
        {
          label:           '수익 (원)',
          data:            revenues,
          backgroundColor: 'rgba(255, 184, 0, 0.72)', /* 브랜드 골드 */
          borderColor:     '#ffb800',
          borderWidth:     1,
          borderRadius:    4,    /* 막대 상단 모서리 둥글게 */
          yAxisID:         'yRevenue',
        },
        /* ── 관람객 수 — 꺾은선 (오른쪽 Y축) ── */
        {
          label:               '관람객 수',
          type:                'line',
          data:                counts,
          borderColor:         '#00ad74',            /* 성공 초록 */
          backgroundColor:     'rgba(0, 173, 116, 0.1)',
          pointBackgroundColor:'#00ad74',
          pointRadius:         4,
          pointHoverRadius:    6,
          tension:             0.3,                  /* 완만한 곡선 */
          yAxisID:             'yCount',
        },
      ],
    },
    options: {
      responsive:          true,
      maintainAspectRatio: false,
      interaction: { mode: 'index', intersect: false }, /* 호버 시 두 데이터셋 동시 표시 */

      plugins: {
        legend: {
          labels: {
            color:   '#4f4537',
            font:    { family: 'Pretendard, sans-serif', size: 13 },
            padding: 20,
          },
        },
        tooltip: {
          callbacks: {
            /* 툴팁 레이블: 단위(원/명) 포함 */
            label: function (ctx) {
              const val = ctx.parsed.y;
              if (ctx.dataset.label?.includes('수익')) {
                return ' ' + val.toLocaleString('ko-KR') + '원';
              }
              return ' ' + val.toLocaleString('ko-KR') + '명';
            },
          },
        },
      },

      scales: {
        /* 왼쪽 Y축: 수익 */
        yRevenue: {
          type:     'linear',
          position: 'left',
          ticks: {
            color:    '#9e9189',
            callback: function (v) { return v.toLocaleString('ko-KR') + '원'; },
          },
          grid: { color: 'rgba(0,0,0,0.06)' },
        },
        /* 오른쪽 Y축: 관람객 */
        yCount: {
          type:     'linear',
          position: 'right',
          ticks: {
            color:    '#9e9189',
            callback: function (v) { return v + '명'; },
          },
          grid: { drawOnChartArea: false }, /* 격자선 좌축과 중복 방지 */
        },
        /* X축 */
        x: {
          ticks: { color: '#9e9189' },
          grid:  { color: 'rgba(0,0,0,0.04)' },
        },
      },
    },
  });
}


/* ══════════════════════════════════════════════════════════════════════════
   6. API 조회 → 차트 렌더링
   HTML onclick 에서 직접 호출되는 전역 함수.
══════════════════════════════════════════════════════════════════════════ */

/**
 * 통계 API 호출 후 차트 갱신.
 *
 * 각 통계 페이지의 조회 버튼 onclick 에서 직접 호출:
 *   fetchAndRenderStats(startDate, endDate)           — 날짜 필터 타입
 *   fetchAndRenderStats(null, null, movieId)          — by-movie
 *
 * @param {string|null} startDate - 'YYYY-MM-DD' (by-movie 일 땐 null)
 * @param {string|null} endDate   - 'YYYY-MM-DD' (by-movie 일 땐 null)
 * @param {string|null} [movieId] - 영화 ID (by-movie 전용, 나머지는 생략)
 */
async function fetchAndRenderStats(startDate, endDate, movieId) {
  const type  = typeof STAT_TYPE !== 'undefined' ? STAT_TYPE : 'daily';
  const today = todayISO();

  /* ── 날짜 유효성 검증 (날짜 필터 있는 타입만) ── */
  if (type !== 'by-movie') {
    if (!startDate || !endDate) {
      CineOS.alert.show('날짜를 선택해 주세요.', 'warning');
      return;
    }
    if (startDate > today || endDate > today) {
      CineOS.alert.show('미래 날짜는 선택할 수 없습니다.', 'warning');
      return;
    }
    if (startDate > endDate) {
      CineOS.alert.show('시작일이 종료일보다 늦을 수 없습니다.', 'warning');
      return;
    }
  }

  try {
    CineOS.loading?.show();

    /* ── API URL 조립 ── */
    /* 실제 엔드포인트: GET /api/admin/stats/{type}?startDate=&endDate=&movieId= */
    const params = new URLSearchParams();
    if (startDate) params.set('startDate', startDate);
    if (endDate)   params.set('endDate',   endDate);
    if (movieId)   params.set('movieId',   String(movieId));

    const qs  = params.toString();
    const url = `/api/admin/stats/${type}` + (qs ? '?' + qs : '');

    const data = await CineOS.api.get(url);
    renderChart(data);

  } catch (err) {
    console.error('[stats.js] 통계 조회 실패:', err);
    CineOS.alert.show('통계 데이터를 불러오지 못했습니다.', 'error');
  } finally {
    CineOS.loading?.hide();
  }
}


/* ══════════════════════════════════════════════════════════════════════════
   7. 초기화 — DOMContentLoaded
══════════════════════════════════════════════════════════════════════════ */

document.addEventListener('DOMContentLoaded', function initStats() {
  const type = typeof STAT_TYPE !== 'undefined' ? STAT_TYPE : 'daily';

  /* 날짜 필터 max/min/기본값 세팅 */
  initDateFilter();

  /* ── Thymeleaf SSR 초기 데이터가 있으면 API 재호출 없이 바로 렌더링 ── */
  const initData = (typeof STAT_DATA !== 'undefined') ? STAT_DATA : [];
  if (Array.isArray(initData) && initData.length > 0) {
    renderChart(initData);
    return; /* 초기 데이터로 렌더 완료 → API 중복 호출 생략 */
  }

  /* ── SSR 데이터 없으면 기본 파라미터로 API 조회 ── */
  if (type === 'by-movie') {
    /* 영화별: movieSelect 현재값 또는 SELECTED_MOVIE_ID 사용 */
    const sel     = document.getElementById('movieSelect');
    const movieId = sel?.value
                 ?? (typeof SELECTED_MOVIE_ID !== 'undefined' ? SELECTED_MOVIE_ID : null);
    if (movieId) fetchAndRenderStats(null, null, String(movieId));

  } else {
    /* 날짜 필터 타입: 초기화된 날짜 범위로 조회 */
    const start = $startDate?.value;
    const end   = $endDate?.value;
    if (start && end) fetchAndRenderStats(start, end);
  }
});
