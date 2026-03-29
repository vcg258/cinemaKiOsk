/**
 * SchedulePage.jsx — 날짜·시간·인원 선택 (UC-03 1~3단계)
 *
 * 동작 흐름:
 *  1. 날짜 선택 (오늘~6일 후, 가로 스크롤)
 *  2. 선택된 날짜의 상영 시간 선택
 *  3. 인원 유형별 수 선택 (성인/청소년/경로/장애인)
 *  4. "다음: 좌석 선택" → SeatPage 로 이동하며 예매 정보 전달
 *
 * state 수신: location.state.movieId, location.state.movieTitle
 *
 * 변경사항:
 *  - 이모지 제거 → Lucide 아이콘 사용
 *  - 다음 버튼: fixed footer 제거 → 인원 선택 섹션 바로 아래 배치 (키오스크 사용성)
 *  - 비활성 버튼에 조건 안내 메시지 표시
 *  - 반말 제거 (UI 텍스트 존댓말)
 * TODO: GET /api/schedules?movieId=&date= 연동
 */
import { useState, useMemo } from 'react'
import { useNavigate, useLocation } from 'react-router-dom'
import { ChevronLeft, Film, Calendar, Clock, Users, ChevronDown, ChevronUp, Info } from 'lucide-react'
import { MOCK_SCHEDULES, SCHEDULE_DATES, MOCK_MOVIES, PERSON_TYPES } from '../../api/mockData'

/** 날짜 포맷: "03/29(토)" */
function fmtDateLabel(dateStr) {
  const d = new Date(dateStr)
  const days = ['일', '월', '화', '수', '목', '금', '토']
  const mm = String(d.getMonth() + 1).padStart(2, '0')
  const dd = String(d.getDate()).padStart(2, '0')
  return `${mm}/${dd}(${days[d.getDay()]})`
}

function SchedulePage() {
  const navigate = useNavigate()
  const location = useLocation()

  // 이전 페이지(MovieDetail)에서 넘겨받은 movieId, movieTitle
  const { movieId, movieTitle } = location.state ?? {}

  // movieId 없으면 홈으로 리다이렉트
  if (!movieId) {
    navigate('/')
    return null
  }

  const movie    = MOCK_MOVIES.find((m) => m.id === movieId)
  const allSched = MOCK_SCHEDULES[movieId] ?? []

  // ── 선택 상태 ──
  const [selectedDate,  setSelectedDate]  = useState(SCHEDULE_DATES[0])
  const [selectedSched, setSelectedSched] = useState(null) // 선택된 상영 일정
  // 인원: { ADULT: 1, TEEN: 0, SENIOR: 0, DISABLED: 0 }
  const [persons, setPersons] = useState({ ADULT: 1, TEEN: 0, SENIOR: 0, DISABLED: 0 })

  // 선택된 날짜의 상영 목록
  const daySchedules = useMemo(
    () => allSched.filter((s) => s.date === selectedDate),
    [allSched, selectedDate]
  )

  /** 날짜 변경 → 시간 선택 초기화 */
  const handleDateChange = (date) => {
    setSelectedDate(date)
    setSelectedSched(null)
  }

  /** 인원 수 변경 (+/-) */
  const changePerson = (type, delta) => {
    setPersons((prev) => {
      const next  = prev[type] + delta
      const total = Object.values({ ...prev, [type]: next }).reduce((a, b) => a + b, 0)
      // 0명 미만 or 8명 초과 불가
      if (next < 0 || total > 8) return prev
      return { ...prev, [type]: next }
    })
  }

  const totalPersons = Object.values(persons).reduce((a, b) => a + b, 0)

  // 다음 버튼 활성화 조건
  const canProceed = selectedSched !== null && totalPersons > 0

  /**
   * 비활성 상태일 때 안내 메시지
   * 어떤 조건을 채워야 다음으로 갈 수 있는지 표시
   */
  const getHintMessage = () => {
    if (!selectedSched && totalPersons === 0) return '관람 시간과 인원을 선택해 주세요.'
    if (!selectedSched) return '관람하실 시간과 상영관을 선택해 주세요.'
    if (totalPersons === 0) return '인원을 1명 이상 선택해 주세요.'
    return ''
  }

  /** 다음 단계 → SeatPage */
  const handleNext = () => {
    if (!canProceed) return
    navigate('/booking/seat', {
      state: {
        movieId,
        movieTitle: movieTitle ?? movie?.title,
        schedule: selectedSched,
        persons,
        totalPersons,
      },
    })
  }

  return (
    <div style={pageWrap}>

      {/* ── 뒤로 가기 ── */}
      <button onClick={() => navigate(-1)} style={backBtn}>
        <ChevronLeft size={20} />
        영화 상세
      </button>

      {/* ── 페이지 제목 ── */}
      <h2 style={pageTitle}>
        <Calendar size={24} style={{ marginRight: 10, verticalAlign: 'middle' }} />
        날짜 · 시간 · 인원 선택
      </h2>

      {/* 영화 제목 배지 */}
      <div style={movieBadge}>
        <Film size={16} style={{ marginRight: 6 }} />
        {movieTitle ?? movie?.title}
      </div>

      {/* ── STEP 1: 날짜 선택 ── */}
      <section style={section}>
        <h3 style={stepTitle}>
          <span style={stepNum}>1</span>
          날짜 선택
        </h3>
        <div style={dateRow}>
          {SCHEDULE_DATES.map((date) => {
            const isToday    = date === new Date().toISOString().slice(0, 10)
            const isSelected = date === selectedDate
            return (
              <button
                key={date}
                onClick={() => handleDateChange(date)}
                style={{ ...dateBtn, ...(isSelected ? dateBtnActive : {}) }}
              >
                {isToday && <span style={todayLabel}>오늘</span>}
                <span style={{ fontSize: 17, fontWeight: isSelected ? 700 : 400 }}>
                  {fmtDateLabel(date)}
                </span>
              </button>
            )
          })}
        </div>
      </section>

      {/* ── STEP 2: 시간 선택 ── */}
      <section style={section}>
        <h3 style={stepTitle}>
          <span style={stepNum}>2</span>
          시간 선택
        </h3>
        {daySchedules.length === 0 ? (
          <p style={{ color: 'var(--text-muted)', fontSize: 15 }}>
            선택하신 날짜에 상영 일정이 없습니다.
          </p>
        ) : (
          <div style={timeGrid}>
            {daySchedules.map((s) => {
              const soldOut    = s.availableSeats === 0
              const isSelected = selectedSched?.scheduleId === s.scheduleId
              return (
                <button
                  key={s.scheduleId}
                  onClick={() => !soldOut && setSelectedSched(s)}
                  disabled={soldOut}
                  style={{
                    ...timeBtn,
                    ...(isSelected ? timeBtnActive : {}),
                    ...(soldOut ? timeBtnSoldOut : {}),
                  }}
                >
                  <p style={{ fontSize: 26, fontWeight: 700, margin: '8px 0 4px' }}>
                    {s.startTime}
                  </p>
                  <p style={{ fontSize: 13, color: 'var(--text-secondary)', margin: 0 }}>
                    {s.theaterName} · ~{s.endTime}
                  </p>
                  <p style={{
                    fontSize: 13,
                    color: soldOut ? '#e03c3c' : '#00ad74',
                    margin: '6px 0 0',
                    fontWeight: 600,
                  }}>
                    {soldOut ? '매진' : `${s.availableSeats}석 남음`}
                  </p>
                </button>
              )
            })}
          </div>
        )}
      </section>

      {/* ── STEP 3: 인원 선택 ── */}
      <section style={section}>
        <h3 style={stepTitle}>
          <span style={stepNum}>3</span>
          인원 선택
          <span style={{ fontSize: 14, color: 'var(--text-muted)', fontWeight: 400, marginLeft: 8 }}>
            (최대 8명)
          </span>
        </h3>
        <div style={personList}>
          {PERSON_TYPES.map(({ type, label, discount }) => (
            <div key={type} style={personRow}>
              <div>
                <span style={{ fontSize: 17, color: 'var(--text-primary)', fontWeight: 600 }}>
                  {label}
                </span>
                {discount > 0 && (
                  <span style={{ fontSize: 13, color: '#00ad74', marginLeft: 8 }}>
                    -{discount.toLocaleString()}원 할인
                  </span>
                )}
              </div>
              <div style={counter}>
                <button
                  onClick={() => changePerson(type, -1)}
                  style={counterBtn}
                  aria-label={`${label} 감소`}
                >
                  <ChevronDown size={20} />
                </button>
                <span style={counterNum}>{persons[type]}</span>
                <button
                  onClick={() => changePerson(type, +1)}
                  style={counterBtn}
                  aria-label={`${label} 증가`}
                >
                  <ChevronUp size={20} />
                </button>
              </div>
            </div>
          ))}
        </div>
        <p style={{ fontSize: 15, color: 'var(--text-secondary)', marginTop: 12 }}>
          총 인원:{' '}
          <strong style={{ color: 'var(--color-brand-400)', fontSize: 17 }}>
            {totalPersons}명
          </strong>
        </p>
      </section>

      {/* ── 다음 단계 버튼 영역 ── */}
      {/* fixed footer 대신 콘텐츠 하단에 크게 배치 (키오스크 사용성) */}
      <div style={nextArea}>
        {/* 선택 요약 또는 조건 안내 */}
        {canProceed ? (
          <div style={summaryBox}>
            <Users size={16} style={{ marginRight: 6 }} />
            {fmtDateLabel(selectedDate)} · {selectedSched.startTime} · {selectedSched.theaterName} · {totalPersons}명
          </div>
        ) : (
          <div style={hintBox}>
            <Info size={16} style={{ marginRight: 6, flexShrink: 0 }} />
            {getHintMessage()}
          </div>
        )}

        <button
          onClick={handleNext}
          disabled={!canProceed}
          style={{
            ...nextBtn,
            ...(!canProceed ? nextBtnDisabled : {}),
          }}
        >
          좌석 선택으로 이동
        </button>
      </div>
    </div>
  )
}

/* ── 스타일 ── */
const pageWrap  = { maxWidth: 900, margin: '0 auto', padding: '32px 40px 80px' }
const backBtn   = {
  display: 'flex', alignItems: 'center', gap: 6,
  background: 'none', border: 'none',
  color: 'var(--text-secondary)', fontSize: 16,
  cursor: 'pointer', padding: '10px 0', marginBottom: 24,
}
const pageTitle = {
  fontSize: 24, fontWeight: 800, color: 'var(--text-primary)', marginBottom: 12,
  display: 'flex', alignItems: 'center',
}
const movieBadge = {
  display: 'inline-flex', alignItems: 'center',
  padding: '8px 18px', background: 'var(--bg-surface)',
  border: '1px solid var(--border-default)', borderRadius: 24,
  color: 'var(--text-secondary)', fontSize: 15, marginBottom: 36,
}
const section   = { marginBottom: 40 }
const stepTitle = {
  fontSize: 18, fontWeight: 700, color: 'var(--color-brand-400)',
  marginBottom: 16, display: 'flex', alignItems: 'center', gap: 10,
}
const stepNum   = {
  display: 'inline-flex', alignItems: 'center', justifyContent: 'center',
  width: 28, height: 28, borderRadius: '50%',
  background: 'var(--color-brand-400)', color: 'var(--color-neutral-900)',
  fontSize: 14, fontWeight: 800, flexShrink: 0,
}

const dateRow   = {
  display: 'flex', gap: 10, overflowX: 'auto', paddingBottom: 8,
}
const dateBtn   = {
  flexShrink: 0, padding: '12px 20px',
  background: 'var(--bg-surface)',
  border: '1px solid var(--border-default)', borderRadius: 12,
  color: 'var(--text-primary)', cursor: 'pointer', textAlign: 'center',
  minWidth: 90, position: 'relative',
  display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 4,
}
const dateBtnActive = { borderColor: 'var(--color-brand-400)', background: 'rgba(255,184,0,0.1)' }
const todayLabel    = {
  fontSize: 11, color: 'var(--color-brand-400)', fontWeight: 700,
}

const timeGrid  = { display: 'flex', gap: 16, flexWrap: 'wrap' }
const timeBtn   = {
  padding: '16px 20px', background: 'var(--bg-surface)',
  border: '1px solid var(--border-default)', borderRadius: 14,
  textAlign: 'center', minWidth: 150, cursor: 'pointer',
  color: 'var(--text-primary)',
}
const timeBtnActive  = { borderColor: 'var(--color-brand-400)', background: 'rgba(255,184,0,0.1)' }
const timeBtnSoldOut = { opacity: 0.4, cursor: 'not-allowed' }

const personList = {
  display: 'flex', flexDirection: 'column', gap: 16,
  background: 'var(--bg-surface)', borderRadius: 16, padding: '20px 24px',
}
const personRow  = {
  display: 'flex', alignItems: 'center', justifyContent: 'space-between',
}
const counter    = { display: 'flex', alignItems: 'center', gap: 16 }
const counterBtn = {
  width: 48, height: 48, borderRadius: '50%',
  border: '1px solid var(--border-default)',
  background: 'var(--bg-base)', color: 'var(--text-primary)',
  cursor: 'pointer',
  display: 'flex', alignItems: 'center', justifyContent: 'center',
}
const counterNum = {
  width: 36, textAlign: 'center',
  fontSize: 22, fontWeight: 700, color: 'var(--text-primary)',
}

/* 다음 버튼 영역 — 키오스크 하단 가까이 크게 배치 */
const nextArea  = {
  marginTop: 16, padding: '32px 0 0',
  borderTop: '1px solid var(--border-subtle)',
}
const summaryBox = {
  display: 'flex', alignItems: 'center',
  padding: '12px 20px', marginBottom: 16,
  background: 'rgba(255,184,0,0.08)',
  border: '1px solid var(--color-brand-400)',
  borderRadius: 12, fontSize: 15, color: 'var(--color-brand-400)', fontWeight: 600,
}
const hintBox   = {
  display: 'flex', alignItems: 'center',
  padding: '12px 20px', marginBottom: 16,
  background: 'var(--bg-surface)',
  border: '1px solid var(--border-default)',
  borderRadius: 12, fontSize: 15, color: 'var(--text-muted)',
}
const nextBtn   = {
  display: 'block', width: '100%',
  padding: '24px 0',
  background: 'var(--btn-primary-bg)', color: 'var(--btn-primary-text)',
  border: 'none', borderRadius: 16,
  fontSize: 22, fontWeight: 800, cursor: 'pointer', letterSpacing: 1,
}
const nextBtnDisabled = {
  background: 'var(--bg-surface)', color: 'var(--text-muted)', cursor: 'not-allowed',
}

export default SchedulePage
