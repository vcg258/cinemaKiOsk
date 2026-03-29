import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
// 폰트 → 토큰(CSS 변수) → 전역 스타일 순서로 import해야 올바르게 적용됨
import './styles/fonts.css'
import './styles/tokens.css'
import './styles/global.css'
import App from './App.jsx'

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <App />
  </StrictMode>,
)
