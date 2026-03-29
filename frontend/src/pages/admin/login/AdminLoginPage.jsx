/**
 * AdminLoginPage.jsx — 관리자 로그인 (UC-11)
 *
 * 동작:
 *  - 아이디/비밀번호 입력
 *  - 로그인 실패 시 경고창(alert) 표시
 *  - 성공 시 /admin/statistics/dashboard 이동
 *
 * 더미 계정: admin / admin123
 * TODO: POST /api/admin/login 연동
 */
import { useState } from 'react'
import { useNavigate } from 'react-router-dom'

function AdminLoginPage() {
  const navigate  = useNavigate()
  const [id,      setId]      = useState('')
  const [pw,      setPw]      = useState('')
  const [loading, setLoading] = useState(false)
  const [error,   setError]   = useState('')

  const handleLogin = async (e) => {
    e.preventDefault()
    setError('')
    setLoading(true)

    // TODO: 실제 API 연동 — POST /api/admin/login { id, password }
    await new Promise((r) => setTimeout(r, 600)) // 네트워크 딜레이 시뮬레이션

    if (id === 'admin' && pw === 'admin123') {
      navigate('/admin/statistics/dashboard')
    } else {
      // UC-11: 실패 시 경고창 (alert 대신 인라인 에러메시지 사용)
      setError('아이디 또는 비밀번호가 틀렸어.')
    }
    setLoading(false)
  }

  return (
    <div style={pageWrap}>
      {/* 로고 */}
      <div style={logoWrap}>
        <span style={logoText}>CineOS</span>
        <span style={logoBadge}>관리자</span>
      </div>

      <h1 style={title}>관리자 로그인</h1>
      <p style={hint}>테스트 계정: admin / admin123</p>

      <form onSubmit={handleLogin} style={form}>
        {/* 아이디 */}
        <div style={fieldGroup}>
          <label style={label}>아이디</label>
          <input
            type="text"
            value={id}
            onChange={(e) => setId(e.target.value)}
            placeholder="관리자 아이디"
            required
            style={input}
            autoComplete="username"
          />
        </div>

        {/* 비밀번호 */}
        <div style={fieldGroup}>
          <label style={label}>비밀번호</label>
          <input
            type="password"
            value={pw}
            onChange={(e) => setPw(e.target.value)}
            placeholder="비밀번호"
            required
            style={input}
            autoComplete="current-password"
          />
        </div>

        {/* 에러 메시지 (UC-11: 로그인 실패 경고) */}
        {error && (
          <div style={errorBox}>
            ⚠️ {error}
          </div>
        )}

        <button type="submit" disabled={loading} style={submitBtn}>
          {loading ? '로그인 중...' : '로그인'}
        </button>
      </form>
    </div>
  )
}

/* ── 스타일 (라이트 테마 — data-theme=light 적용됨) ── */
const pageWrap  = { minHeight: '100vh', display: 'flex', flexDirection: 'column',
                    alignItems: 'center', justifyContent: 'center',
                    background: '#f5f3ef', padding: '24px 16px' }
const logoWrap  = { display: 'flex', alignItems: 'center', gap: 10, marginBottom: 8 }
const logoText  = { fontSize: 28, fontWeight: 900, color: '#0e0b08', letterSpacing: -1 }
const logoBadge = { padding: '3px 10px', background: '#ffb800', borderRadius: 20,
                    fontSize: 12, fontWeight: 700, color: '#4c1c00' }
const title     = { fontSize: 20, fontWeight: 700, color: '#0e0b08', marginBottom: 4 }
const hint      = { fontSize: 13, color: '#b6a999', marginBottom: 28 }
const form      = { width: '100%', maxWidth: 380, display: 'flex', flexDirection: 'column', gap: 16 }
const fieldGroup= { display: 'flex', flexDirection: 'column', gap: 6 }
const label     = { fontSize: 13, fontWeight: 600, color: '#4f4537' }
const input     = { padding: '12px 14px', border: '1px solid #dfe0df', borderRadius: 8,
                    fontSize: 15, background: '#fff', color: '#0e0b08', outline: 'none' }
const errorBox  = { padding: '12px 16px', background: '#fdeaea', border: '1px solid #e03c3c',
                    borderRadius: 8, color: '#a82020', fontSize: 14 }
const submitBtn = { padding: '14px 0', background: '#ffb800', color: '#4c1c00',
                    border: 'none', borderRadius: 10, fontSize: 16, fontWeight: 700,
                    cursor: 'pointer', marginTop: 4 }

export default AdminLoginPage
