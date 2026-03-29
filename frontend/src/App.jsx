/**
 * App.jsx — CineOS 라우터 및 페이지 전환 설정
 *
 * 구조:
 *   BrowserRouter
 *   └── AnimatePresence (페이지 전환 애니메이션 관리)
 *       └── Routes (현재 URL에 맞는 Route 렌더링)
 *           ├── CustomerLayout (고객 영역 공통 레이아웃)
 *           │   ├── /               → HomePage (홈)
 *           │   ├── /movie/list     → MovieListPage (영화 목록)
 *           │   ├── /movie/detail/:id → MovieDetailPage (영화 상세)
 *           │   ├── /booking/schedule → SchedulePage (날짜/시간 선택)
 *           │   ├── /booking/seat   → SeatPage (좌석 선택)
 *           │   ├── /payment        → PaymentPage (결제)
 *           │   └── /payment/result → PaymentResultPage (결제 완료)
 *           └── AdminLayout (관리자 영역 공통 레이아웃)
 *               ├── /admin/login    → AdminLoginPage
 *               ├── /admin/statistics/dashboard → StatsDashboardPage
 *               ├── /admin/statistics/stats/... → 각 통계 페이지
 *               ├── /admin/management/movie/... → 영화 관리 페이지
 *               ├── /admin/management/theater/... → 상영관 관리 페이지
 *               ├── /admin/management/seat/... → 좌석 관리 페이지
 *               ├── /admin/management/policy/... → 정책 관리 페이지
 *               └── /admin/refund   → RefundPage (환불 처리)
 */
import { BrowserRouter, Routes, Route, useLocation } from 'react-router-dom'
import { AnimatePresence } from 'framer-motion'

// 레이아웃 컴포넌트
import CustomerLayout from './components/Layout/CustomerLayout'
import AdminLayout from './components/Layout/AdminLayout'

// 고객 페이지
import HomePage from './pages/movie/HomePage'
import MovieListPage from './pages/movie/MovieListPage'
import MovieDetailPage from './pages/movie/MovieDetailPage'
import SchedulePage from './pages/booking/SchedulePage'
import SeatPage from './pages/booking/SeatPage'
import PaymentPage from './pages/payment/PaymentPage'
import PaymentResultPage from './pages/payment/PaymentResultPage'

// 관리자 페이지
import AdminLoginPage from './pages/admin/login/AdminLoginPage'
import StatsDashboardPage from './pages/admin/statistics/StatsDashboardPage'
import StatsDailyPage from './pages/admin/statistics/StatsDailyPage'
import StatsMonthlyPage from './pages/admin/statistics/StatsMonthlyPage'
import StatsByDayPage from './pages/admin/statistics/StatsByDayPage'
import StatsByHourPage from './pages/admin/statistics/StatsByHourPage'
import StatsByMoviePage from './pages/admin/statistics/StatsByMoviePage'
import MovieListAdminPage from './pages/admin/management/MovieListAdminPage'
import MovieFormPage from './pages/admin/management/MovieFormPage'
import MovieManagePage from './pages/admin/management/MovieManagePage'
import TheaterListPage from './pages/admin/management/TheaterListPage'
import TheaterEditPage from './pages/admin/management/TheaterEditPage'
import SeatListPage from './pages/admin/management/SeatListPage'
import SeatEditPage from './pages/admin/management/SeatEditPage'
import PolicyListPage from './pages/admin/management/PolicyListPage'
import PolicyFormPage from './pages/admin/management/PolicyFormPage'
import PolicyManagePage from './pages/admin/management/PolicyManagePage'
import RefundPage from './pages/admin/management/RefundPage'

/**
 * AnimatedRoutes 컴포넌트
 *
 * AnimatePresence 는 자식 컴포넌트가 언마운트될 때도 exit 애니메이션을 실행하게 해줌.
 * useLocation 으로 현재 경로를 key 로 넘겨야 URL이 바뀔 때마다 재렌더링이 트리거됨.
 * 이 key 가 없으면 같은 레이아웃 내에서 페이지가 바뀌어도 애니메이션이 동작 안 함.
 */
function AnimatedRoutes() {
  // location.key 가 바뀔 때마다 AnimatePresence 가 exit → enter 애니메이션 실행
  const location = useLocation()

  return (
    /* mode="wait": 이전 페이지의 exit 애니메이션이 끝난 후 다음 페이지 enter 시작 */
    <AnimatePresence mode="wait">
      {/*
        key={location.pathname} 을 넘겨야 라우트 변경 시 컴포넌트를 새로 마운트함.
        key 없으면 같은 컴포넌트 재사용 → exit 애니메이션 미동작.
      */}
      <Routes location={location} key={location.pathname}>

        {/* ─── 고객 영역 ─── */}
        <Route element={<CustomerLayout />}>
          <Route index element={<HomePage />} />
          <Route path="movie/list" element={<MovieListPage />} />
          <Route path="movie/detail/:id" element={<MovieDetailPage />} />
          <Route path="booking/schedule" element={<SchedulePage />} />
          <Route path="booking/seat" element={<SeatPage />} />
          <Route path="payment" element={<PaymentPage />} />
          <Route path="payment/result" element={<PaymentResultPage />} />
        </Route>

        {/* ─── 관리자 영역 ─── */}
        <Route path="admin">
          {/* 로그인은 별도 레이아웃 없이 단독 페이지 */}
          <Route path="login" element={<AdminLoginPage />} />

          {/* 통계 & 관리 페이지는 AdminLayout 사용 */}
          <Route element={<AdminLayout />}>
            <Route path="statistics/dashboard" element={<StatsDashboardPage />} />
            <Route path="statistics/stats/daily"   element={<StatsDailyPage />} />
            <Route path="statistics/stats/monthly" element={<StatsMonthlyPage />} />
            <Route path="statistics/stats/by-day"  element={<StatsByDayPage />} />
            <Route path="statistics/stats/by-hour" element={<StatsByHourPage />} />
            <Route path="statistics/stats/by-movie" element={<StatsByMoviePage />} />

            <Route path="management/movie/list"   element={<MovieListAdminPage />} />
            <Route path="management/movie/form"   element={<MovieFormPage />} />
            <Route path="management/movie/manage" element={<MovieManagePage />} />

            <Route path="management/theater/list" element={<TheaterListPage />} />
            <Route path="management/theater/edit" element={<TheaterEditPage />} />

            <Route path="management/seat/list"    element={<SeatListPage />} />
            <Route path="management/seat/edit"    element={<SeatEditPage />} />

            <Route path="management/policy/list"   element={<PolicyListPage />} />
            <Route path="management/policy/form"   element={<PolicyFormPage />} />
            <Route path="management/policy/manage" element={<PolicyManagePage />} />

            <Route path="refund" element={<RefundPage />} />
          </Route>
        </Route>

      </Routes>
    </AnimatePresence>
  )
}

/**
 * 앱 루트 컴포넌트
 * BrowserRouter 로 감싸야 useLocation, Link 등 라우터 훅이 동작함
 */
function App() {
  return (
    <BrowserRouter>
      <AnimatedRoutes />
    </BrowserRouter>
  )
}

export default App
