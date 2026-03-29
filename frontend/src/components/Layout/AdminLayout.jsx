/**
 * AdminLayout.jsx — 관리자 영역 공통 레이아웃
 *
 * 기존 admin-base.html 의 React 버전.
 * - 좌측 사이드바 (240px)
 * - 상단 헤더 (로고 + 로그아웃)
 * - 메인 콘텐츠 영역 (Outlet)
 *
 * data-theme="light" 를 body 에 붙여 라이트 테마 CSS 변수 적용.
 */
import { useEffect, useState } from 'react'
import { Outlet, NavLink, useNavigate } from 'react-router-dom'
import { motion } from 'framer-motion'
import {
  Film, PlaySquare, Armchair, ScrollText, RotateCcw,
  LayoutDashboard, CalendarDays, Calendar, BarChart2, Clock, TrendingUp,
  Sun, Moon,
} from 'lucide-react'
import { adminPageVariants, adminPageTransition } from '../../styles/transitions'
import styles from './AdminLayout.module.css'

/**
 * 사이드바 네비게이션 메뉴 구성
 * 통계 섹션은 가장 하단에 배치
 */
const NAV_ITEMS = [
  { section: '영화 관리', items: [
    { path: '/admin/management/movie/list',   label: '영화 목록',      Icon: Film },
    { path: '/admin/management/movie/form',   label: '영화 등록',      Icon: Film },
    { path: '/admin/management/movie/manage', label: '상영 관리',      Icon: PlaySquare },
  ]},
  { section: '상영관/좌석', items: [
    { path: '/admin/management/theater/list', label: '상영관 목록',    Icon: Armchair },
    // { path: '/admin/management/theater/edit', label: '상영관 수정',    Icon: Armchair },
    { path: '/admin/management/seat/list',    label: '좌석 목록',      Icon: Armchair },
    { path: '/admin/management/seat/edit',    label: '좌석 정책 수정', Icon: Armchair },
  ]},
  { section: '정책/환불', items: [
    { path: '/admin/management/policy/list',   label: '정책 목록', Icon: ScrollText },
    { path: '/admin/management/policy/form',   label: '정책 등록', Icon: ScrollText },
    { path: '/admin/management/policy/manage', label: '정책 수정', Icon: ScrollText },
    { path: '/admin/refund',                   label: '환불 처리', Icon: RotateCcw },
  ]},
  { section: '통계', items: [
    { path: '/admin/statistics/dashboard',         label: '대시보드',      Icon: LayoutDashboard },
    { path: '/admin/statistics/stats/daily',       label: '일일 통계',     Icon: CalendarDays },
    { path: '/admin/statistics/stats/monthly',     label: '월별 통계',     Icon: Calendar },
    { path: '/admin/statistics/stats/by-day',      label: '요일별 통계',   Icon: BarChart2 },
    { path: '/admin/statistics/stats/by-hour',     label: '시간대별 통계', Icon: Clock },
    { path: '/admin/statistics/stats/by-movie',    label: '영화별 통계',   Icon: TrendingUp },
  ]},
]

function AdminLayout() {
  const navigate = useNavigate()

  // 다크모드 상태 — localStorage에서 복원 (기본값: 라이트)
  const [isDark, setIsDark] = useState(
    () => localStorage.getItem('adminTheme') === 'dark'
  )

  // isDark 변경 시 body의 data-theme 속성 교체
  useEffect(() => {
    if (isDark) {
      // 다크: data-theme 제거 → :root 기본값(다크 웜) 사용
      delete document.body.dataset.theme
    } else {
      // 라이트: data-theme="light" 오버라이드 적용
      document.body.dataset.theme = 'light'
    }
    localStorage.setItem('adminTheme', isDark ? 'dark' : 'light')

    return () => {
      // 관리자 페이지 벗어날 때 다크 테마로 복원
      delete document.body.dataset.theme
    }
  }, [isDark])

  // 로그아웃 처리 (TODO: 백엔드 API 연동 후 실제 세션 만료 처리)
  const handleLogout = () => {
    // TODO: POST /api/admin/logout 연동
    navigate('/admin/login')
  }

  return (
    <div className={styles.layout}>

      {/* ── 좌측 사이드바 ── */}
      <aside className={styles.sidebar}>
        <div className={styles.sidebarLogo}>
          <img src="/logo_cineos.svg" alt="CineOS" className={styles.logo} />
          <span className={styles.adminBadge}>관리자</span>
        </div>

        {/* 섹션별 네비게이션 */}
        <nav className={styles.nav}>
          {NAV_ITEMS.map((section) => (
            <div key={section.section} className={styles.navSection}>
              {/* 섹션 제목 */}
              <p className={styles.navSectionTitle}>{section.section}</p>

              {section.items.map(({ path, label, Icon }) => (
                <NavLink
                  key={path}
                  to={path}
                  end
                  className={({ isActive }) =>
                    `${styles.navItem} ${isActive ? styles.navItemActive : ''}`
                  }
                >
                  {/* Lucide 아이콘 + 라벨 */}
                  {Icon && <Icon size={15} style={{ flexShrink: 0 }} />}
                  {label}
                </NavLink>
              ))}
            </div>
          ))}
        </nav>
      </aside>

      {/* ── 우측 메인 영역 ── */}
      <div className={styles.content}>

        {/* 상단 헤더 */}
        <header className={styles.header}>
          <h1 className={styles.pageTitle}>관리자 페이지</h1>
          <div className={styles.headerActions}>
            {/* 다크모드 토글 버튼 */}
            <button
              onClick={() => setIsDark((d) => !d)}
              className={styles.themeBtn}
              title={isDark ? '라이트 모드로 전환' : '다크 모드로 전환'}
            >
              {isDark ? <Sun size={16} /> : <Moon size={16} />}
            </button>
            <button onClick={handleLogout} className={styles.logoutBtn}>
              로그아웃
            </button>
          </div>
        </header>

        {/* 페이지 콘텐츠 */}
        <main className={styles.main}>
          {/*
            motion.div: 관리자 페이지 전환 애니메이션 (고객 영역보다 subtle하게)
          */}
          <motion.div
            className={styles.pageWrapper}
            variants={adminPageVariants}
            initial="initial"
            animate="animate"
            exit="exit"
            transition={adminPageTransition}
          >
            <Outlet />
          </motion.div>
        </main>

      </div>
    </div>
  )
}

export default AdminLayout
