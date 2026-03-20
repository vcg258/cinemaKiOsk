/**
 * static/js/admin/stats.js
 * ─────────────────────────────────────────────────────────────────────────
 * UC-12~16: 통계 차트 공통 초기화 모듈 (Chart.js)
 *
 * ▶ 기능 요약
 *   - 날짜 범위 선택기 초기화 (미래 날짜 선택 불가)
 *   - GET /api/admin/stats/{type}?startDate=&endDate= 로 데이터 조회
 *   - Chart.js로 막대/선 차트 렌더링
 *   - 각 통계 페이지에서 STAT_TYPE 변수 선언 후 이 파일 로드
 *
 * ▶ 사용 방법 (각 통계 html에서)
 *   <script th:inline="javascript">
 *     const STAT_TYPE = 'daily';  // 'daily' | 'monthly' | 'by-day' | 'by-hour' | 'by-movie'
 *   </script>
 *   <script th:src="@{/js/admin/stats.js}"></script>
 *
 * ▶ 연결 template
 *   templates/admin/statistics/stats/*.html
 *
 * ▶ 의존
 *   common.js  → CineOS.api, CineOS.alert
 *   Chart.js   → CDN (admin-base.html에 포함)
 * ─────────────────────────────────────────────────────────────────────────
 */

'use strict';

/* ══════════════════════════════════════════════════════════════════════════
   상수 및 DOM 참조
══════════════════════════════════════════════════════════════════════════ */

/** 통계 조회 API 기본 경로 */
const STATS_API_BASE = '/api/admin/stats';

/** 날짜 필터 폼 엘리먼트 */
const $dateFilterForm  = document.getElementById('date-filter-form');
const $inputStartDate  = document.getElementById('start-date');
const $inputEndDate    = document.getElementById('end-date');
const $btnApplyFilter  = document.getElementById('btn-apply-filter');

/** 차트 캔버스 */
const $chartCanvas     = document.getElementById('stats-chart');

/** Chart.js 인스턴스 (재렌더링 시 destroy 후 재생성) */
let chartInstance = null;


/* ══════════════════════════════════════════════════════════════════════════
   날짜 필터 초기화
══════════════════════════════════════════════════════════════════════════ */

/**
 * 날짜 입력 필드 초기값 설정 및 미래 날짜 선택 방지.
 * - 기본 범위: 오늘 기준 최근 7일
 * - max 속성을 오늘 날짜로 고정
 */
function initDateFilter() {
  const today = new Date();

  // 오늘 날짜 문자열 (YYYY-MM-DD)
  const todayStr = formatDateISO(today);

  // 7일 전 날짜
  const weekAgo = new Date(today);
  weekAgo.setDate(today.getDate() - 6);
  const weekAgoStr = formatDateISO(weekAgo);

  // 초기값 설정
  if ($inputStartDate) {
    $inputStartDate.value = weekAgoStr;
    $inputStartDate.max   = todayStr; // 미래 날짜 선택 불가
  }
  if ($inputEndDate) {
    $inputEndDate.value = todayStr;
    $inputEndDate.max   = todayStr;
  }

  // 시작일 변경 시 종료일 min 갱신 (역전 방지)
  $inputStartDate?.addEventListener('change', () => {
    if ($inputEndDate) {
      $inputEndDate.min = $inputStartDate.value;
      // 종료일이 시작일보다 앞이면 시작일로 맞춤
      if ($inputEndDate.value < $inputStartDate.value) {
        $inputEndDate.value = $inputStartDate.value;
      }
    }
  });
}

/**
 * Date 객체를 'YYYY-MM-DD' 형식 문자열로 변환.
 * @param {Date} date
 * @returns {string}
 */
function formatDateISO(date) {
  const yyyy = date.getFullYear();
  const mm   = String(date.getMonth() + 1).padStart(2, '0');
  const dd   = String(date.getDate()).padStart(2, '0');
  return `${yyyy}-${mm}-${dd}`;
}


/* ══════════════════════════════════════════════════════════════════════════
   데이터 조회
══════════════════════════════════════════════════════════════════════════ */

/**
 * 통계 API 호출 후 차트 렌더링.
 * @param {string} startDate - 'YYYY-MM-DD'
 * @param {string} endDate   - 'YYYY-MM-DD'
 */
async function fetchAndRenderStats(startDate, endDate) {
  // 미래 날짜 선택 방지 검증
  const today = formatDateISO(new Date());
  if (startDate > today || endDate > today) {
    CineOS.alert.show('미래 날짜는 선택할 수 없습니다.', 'warning');
    return;
  }
  if (startDate > endDate) {
    CineOS.alert.show('시작일이 종료일보다 늦을 수 없습니다.', 'warning');
    return;
  }

  try {
    // 로딩 표시
    CineOS.loading?.show();

    // API 호출: GET /api/admin/stats/{type}?startDate=&endDate=
    // STAT_TYPE은 각 통계 페이지에서 th:inline="javascript"로 선언
    const type = (typeof STAT_TYPE !== 'undefined') ? STAT_TYPE : 'daily';
    const url  = `${STATS_API_BASE}/${type}?startDate=${startDate}&endDate=${endDate}`;

    const data = await CineOS.api.get(url);

    // 차트 렌더링
    renderChart(data, type);

  } catch (err) {
    console.error('[stats.js] 통계 조회 실패:', err);
    CineOS.alert.show('통계 데이터를 불러오지 못했습니다.', 'error');
  } finally {
    CineOS.loading?.hide();
  }
}


/* ══════════════════════════════════════════════════════════════════════════
   Chart.js 렌더링
══════════════════════════════════════════════════════════════════════════ */

/**
 * Chart.js로 통계 차트 렌더링.
 * 기존 차트가 있으면 destroy 후 재생성.
 *
 * @param {Object} data  - API 응답 데이터 (StatisticsDTO 배열 등)
 * @param {string} type  - 차트 유형
 */
function renderChart(data, type) {
  if (!$chartCanvas) return;

  // 기존 차트 인스턴스 정리
  if (chartInstance) {
    chartInstance.destroy();
    chartInstance = null;
  }

  // 데이터 파싱 (백엔드 응답 구조에 맞게 조정 필요)
  const labels   = data.map(item => item.label  ?? item.day   ?? item.date  ?? '');
  const revenues = data.map(item => item.revenue ?? 0);
  const counts   = data.map(item => item.customerCount ?? 0);

  // Chart.js 설정
  const config = {
    type: 'bar',
    data: {
      labels,
      datasets: [
        {
          label: '수익 (원)',
          data: revenues,
          backgroundColor: 'rgba(255, 184, 0, 0.75)', // 브랜드 골드
          borderColor:     '#ffb800',
          borderWidth: 1,
          yAxisID: 'yRevenue',
        },
        {
          label: '관람객 수',
          type: 'line',
          data: counts,
          borderColor:     '#00ad74', // 성공 초록
          backgroundColor: 'rgba(0, 173, 116, 0.1)',
          pointBackgroundColor: '#00ad74',
          tension: 0.3,
          yAxisID: 'yCount',
        },
      ],
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      interaction: { mode: 'index', intersect: false },
      plugins: {
        legend: {
          labels: { color: '#4f4537', font: { family: 'Pretendard' } },
        },
        tooltip: {
          callbacks: {
            // 수익 항목: 원 단위 포맷
            label: (ctx) => {
              const val = ctx.parsed.y;
              if (ctx.dataset.label?.includes('수익')) {
                return ` ${val.toLocaleString('ko-KR')}원`;
              }
              return ` ${val.toLocaleString('ko-KR')}명`;
            },
          },
        },
      },
      scales: {
        // 왼쪽 Y축: 수익
        yRevenue: {
          type: 'linear',
          position: 'left',
          ticks: {
            color: '#9e9189',
            callback: (val) => `${val.toLocaleString('ko-KR')}원`,
          },
          grid: { color: 'rgba(0,0,0,0.06)' },
        },
        // 오른쪽 Y축: 관람객
        yCount: {
          type: 'linear',
          position: 'right',
          ticks: {
            color: '#9e9189',
            callback: (val) => `${val}명`,
          },
          grid: { drawOnChartArea: false },
        },
        x: {
          ticks: { color: '#9e9189' },
          grid:  { color: 'rgba(0,0,0,0.04)' },
        },
      },
    },
  };

  chartInstance = new Chart($chartCanvas, config);
}


/* ══════════════════════════════════════════════════════════════════════════
   이벤트 바인딩 및 초기화
══════════════════════════════════════════════════════════════════════════ */

/**
 * 날짜 필터 적용 버튼 클릭 이벤트.
 * 폼 submit 또는 버튼 클릭 모두 처리.
 */
function bindFilterEvent() {
  const handler = (e) => {
    e?.preventDefault();
    const start = $inputStartDate?.value;
    const end   = $inputEndDate?.value;
    if (start && end) {
      fetchAndRenderStats(start, end);
    }
  };

  $dateFilterForm?.addEventListener('submit', handler);
  $btnApplyFilter?.addEventListener('click',  handler);
}

/** 페이지 로드 시 초기화 */
document.addEventListener('DOMContentLoaded', () => {
  initDateFilter();
  bindFilterEvent();

  // 페이지 진입 시 기본 범위로 즉시 조회
  const start = $inputStartDate?.value;
  const end   = $inputEndDate?.value;
  if (start && end) {
    fetchAndRenderStats(start, end);
  }
});
