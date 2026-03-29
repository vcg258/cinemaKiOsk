/**
 * MovieListPage.jsx — 상영작 목록 페이지 (UC-01)
 *
 * 기능:
 *  - 탭: 현재 상영 중 / 상영 예정 전환
 *  - 필터: 장르 · 등급 (단일 선택 칩)
 *  - 검색: 키워드로 영화 제목 필터링 (터치 키보드 연동)
 *  - 카드 그리드: 포스터 · 제목 · 장르 · 등급 배지 · 런타임
 *  - 카드 클릭 → UC-02 영화 상세 페이지로 이동
 *
 * 터치 키보드:
 *  - 검색 input 포커스(터치) 시 useKeyboard().openKeyboard() 호출
 *  - KeyboardContext 를 통해 전역 TouchKeyboard 컴포넌트가 하단에 표시됨
 *
 * FHD(1080×1920) 세로형 키오스크 기준 레이아웃
 */
import { useState, useMemo } from 'react'
import { useNavigate } from 'react-router-dom'
import { Search, X, Film } from 'lucide-react'
import {
  NOW_PLAYING, UPCOMING,
  GENRE_OPTIONS, RATING_OPTIONS,
} from '../../api/mockData'
import { useKeyboard } from '../../context/KeyboardContext'
import styles from './MovieListPage.module.css'

/** 등급 → 표시 텍스트 (카드용 짧은 형식) */
const RATING_LABEL = {
  ALL:  '전체관람가',
  '12': '12세',
  '15': '15세',
  '19': '청불',
}

/** 런타임(분) → "2시간 46분" 형식 변환 */
function formatRuntime(minutes) {
  if (!minutes) return ''
  const h = Math.floor(minutes / 60)
  const m = minutes % 60
  return h > 0 ? `${h}시간 ${m > 0 ? `${m}분` : ''}` : `${m}분`
}

function MovieListPage() {
  const navigate = useNavigate()
  // KeyboardContext: 터치 키보드 열기 함수
  const { openKeyboard } = useKeyboard()

  // 현재 활성 탭: 'now' | 'upcoming'
  const [activeTab, setActiveTab] = useState('now')

  // 필터 상태
  const [selectedGenre,  setSelectedGenre]  = useState('전체')
  const [selectedRating, setSelectedRating] = useState('')
  const [searchQuery,    setSearchQuery]    = useState('')

  // 탭에 따라 기본 목록 결정
  const baseList = activeTab === 'now' ? NOW_PLAYING : UPCOMING

  /**
   * useMemo로 필터링 결과 메모이제이션
   * baseList, 필터 상태가 바뀔 때만 재계산
   */
  const filteredMovies = useMemo(() => {
    return baseList.filter(movie => {
      if (selectedGenre !== '전체' && !movie.genre.includes(selectedGenre)) return false
      if (selectedRating && movie.rating !== selectedRating) return false
      if (searchQuery.trim() && !movie.title.includes(searchQuery.trim())) return false
      return true
    })
  }, [baseList, selectedGenre, selectedRating, searchQuery])

  /** 탭 전환 시 필터 초기화 */
  const handleTabChange = (tab) => {
    setActiveTab(tab)
    setSelectedGenre('전체')
    setSelectedRating('')
    setSearchQuery('')
  }

  /** 카드 클릭 → 영화 상세 페이지 */
  const handleCardClick = (movieId) => {
    navigate(`/movie/detail/${movieId}`)
  }

  /**
   * 검색 input 터치(포커스) 핸들러
   * openKeyboard(inputEl, 현재값, setState) 호출 → 하단 터치 키보드 표시
   * 키보드에서 입력하면 setSearchQuery 가 호출되어 상태가 업데이트됨
   */
  const handleSearchFocus = (e) => {
    openKeyboard(e.target, searchQuery, setSearchQuery)
  }

  return (
    <div className={styles.page}>

      {/* ── 페이지 헤더 ── */}
      <div className={styles.pageHeader}>
        <h1 className={styles.pageTitle}>영화</h1>
      </div>

      {/* ── 탭: 현재 상영 중 / 상영 예정 ── */}
      <div className={styles.tabs} role="tablist" aria-label="상영 구분">
        <button
          type="button"
          role="tab"
          aria-selected={activeTab === 'now'}
          className={`${styles.tab} ${activeTab === 'now' ? styles.tabActive : ''}`}
          onClick={() => handleTabChange('now')}
        >
          현재 상영 중
        </button>
        <button
          type="button"
          role="tab"
          aria-selected={activeTab === 'upcoming'}
          className={`${styles.tab} ${activeTab === 'upcoming' ? styles.tabActive : ''}`}
          onClick={() => handleTabChange('upcoming')}
        >
          상영 예정
        </button>
      </div>

      {/* ── 필터 바 ── */}
      <section className={styles.filterBar} aria-label="필터 및 검색">

        {/* 장르 필터 */}
        <div className={styles.filterRow}>
          <span className={styles.filterLabel}>장르</span>
          <div className={styles.chipGroup} role="group">
            {GENRE_OPTIONS.map(genre => (
              <button
                key={genre}
                type="button"
                className={`${styles.chip} ${selectedGenre === genre ? styles.chipActive : ''}`}
                onClick={() => setSelectedGenre(genre)}
              >
                {genre}
              </button>
            ))}
          </div>
        </div>

        {/* 등급 필터 */}
        <div className={styles.filterRow}>
          <span className={styles.filterLabel}>등급</span>
          <div className={styles.chipGroup} role="group">
            {RATING_OPTIONS.map(opt => (
              <button
                key={opt.value}
                type="button"
                className={`${styles.chip} ${selectedRating === opt.value ? styles.chipActive : ''}`}
                onClick={() => setSelectedRating(opt.value)}
              >
                {opt.label}
              </button>
            ))}
          </div>
        </div>

        {/* 검색 — 터치 시 터치 키보드 팝업 */}
        <div className={`${styles.filterRow} ${styles.filterRowSearch}`}>
          <span className={styles.filterLabel}>검색</span>
          <div className={styles.searchWrap}>
            <Search size={18} className={styles.searchIcon} />
            <input
              type="text"
              className={styles.searchInput}
              placeholder="영화 제목을 입력해 주세요"
              value={searchQuery}
              onChange={e => setSearchQuery(e.target.value)}
              onFocus={handleSearchFocus}  /* 터치(포커스) 시 터치 키보드 열기 */
              autoComplete="off"
              maxLength={50}
            />
            {/* X 버튼: 검색어 있을 때만 표시 */}
            {searchQuery && (
              <button
                type="button"
                className={styles.searchClear}
                onClick={() => setSearchQuery('')}
                aria-label="검색어 지우기"
              >
                <X size={18} />
              </button>
            )}
          </div>
        </div>

      </section>

      {/* ── 결과 영역 ── */}
      <section className={styles.resultArea} role="tabpanel" aria-live="polite">
        {filteredMovies.length === 0 ? (
          /* 빈 결과 */
          <div className={styles.empty}>
            <Film size={52} color="var(--text-muted)" />
            <p className={styles.emptyText}>
              {searchQuery
                ? `"${searchQuery}" 검색 결과가 없습니다.`
                : activeTab === 'now'
                  ? '현재 상영 중인 영화가 없습니다.'
                  : '상영 예정 영화가 없습니다.'}
            </p>
          </div>
        ) : (
          /* 영화 카드 그리드 */
          <ul className={styles.grid} aria-label="영화 목록">
            {filteredMovies.map(movie => (
              <li key={movie.id}>
                <article
                  className={styles.card}
                  onClick={() => handleCardClick(movie.id)}
                  role="button"
                  tabIndex={0}
                  onKeyDown={e => e.key === 'Enter' && handleCardClick(movie.id)}
                  aria-label={`${movie.title} 상세 보기`}
                >
                  {/* 포스터 이미지 */}
                  <div className={styles.cardImgWrap}>
                    <img
                      className={styles.cardImg}
                      src={movie.posterUrl || '/placeholder-poster.jpg'}
                      alt={`${movie.title} 포스터`}
                      onError={e => { e.target.src = '/placeholder-poster.jpg' }}
                    />
                  </div>

                  {/* 카드 텍스트 */}
                  <div className={styles.cardBody}>
                    <h2 className={styles.cardTitle}>{movie.title}</h2>
                    <div className={styles.cardMeta}>
                      <span className={`${styles.badge} ${styles[`badge${movie.rating}`]}`}>
                        {RATING_LABEL[movie.rating] ?? movie.rating}
                      </span>
                      <span className={styles.cardGenre}>{movie.genre}</span>
                    </div>
                    <p className={styles.cardRuntime}>{formatRuntime(movie.runtime)}</p>
                  </div>
                </article>
              </li>
            ))}
          </ul>
        )}
      </section>

    </div>
  )
}

export default MovieListPage
