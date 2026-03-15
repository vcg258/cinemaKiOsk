/**
 * common.js
 * ─────────────────────────────────────────────────────────────────────────
 * CineOS 키오스크 공용 유틸리티 모듈.
 * 전역 네임스페이스 CineOS 에 아래 API를 등록:
 *
 *   CineOS.api    — fetch/axios 래퍼 (GET, POST, PUT, DELETE)
 *   CineOS.modal  — 공용 모달 제어 (open, close, confirm, alert)
 *   CineOS.alert  — 토스트 알림 제어 (show, dismiss)
 *   CineOS.util   — 공용 포맷/유효성 유틸 함수
 *   CineOS.loading — 전체 화면 스피너 제어
 * ─────────────────────────────────────────────────────────────────────────
 */

// ── 전역 네임스페이스 선언 ──────────────────────────────────────────────────
const CineOS = (function () {
  'use strict';

  /* =====================================================================
     1. API 모듈 — REST API 호출 래퍼
     axios 기반. CDN에서 axios 로드 실패 시 fetch fallback 포함.
     ===================================================================== */
  const api = (() => {

    /**
     * axios 또는 fetch로 HTTP 요청 수행.
     * 성공: 응답 data 반환 (axios.data 혹은 Response.json()).
     * 실패: Error를 throw (catch 블록에서 처리 필요).
     *
     * @param {'GET'|'POST'|'PUT'|'DELETE'} method HTTP 메서드
     * @param {string} url  요청 URL
     * @param {Object|null} data  요청 바디 (POST/PUT 시 사용)
     * @returns {Promise<any>} 응답 데이터
     */
    async function _request(method, url, data = null) {
      // axios 로드 여부 확인
      if (typeof axios !== 'undefined') {
        // ── axios 사용 ──────────────────────────────────────────────────
        const response = await axios({
          method,
          url,
          data,
          // CSRF 토큰 자동 헤더 포함 (Spring Security 기본 헤더명)
          headers: {
            'Content-Type': 'application/json',
            ..._getCsrfHeader(),
          },
        });
        return response.data;

      } else {
        // ── fetch fallback ─────────────────────────────────────────────
        const options = {
          method,
          headers: {
            'Content-Type': 'application/json',
            ..._getCsrfHeader(),
          },
        };
        if (data) options.body = JSON.stringify(data);

        const response = await fetch(url, options);

        // HTTP 오류 응답 처리
        if (!response.ok) {
          const errBody = await response.json().catch(() => ({}));
          const error = new Error(errBody.message || `HTTP ${response.status}`);
          error.status = response.status;
          error.data   = errBody;
          throw error;
        }

        // 204 No Content는 빈 객체 반환
        if (response.status === 204) return {};
        return response.json();
      }
    }

    /**
     * Spring Security CSRF 토큰을 meta 태그에서 읽어 헤더 객체로 반환.
     * CSRF 미설정 환경에서는 빈 객체 반환.
     *
     * HTML에 아래 meta 태그가 있어야 함 (Thymeleaf에서 자동 주입 가능):
     *   <meta name="_csrf"        content="${_csrf.token}">
     *   <meta name="_csrf_header" content="${_csrf.headerName}">
     *
     * @returns {Object} CSRF 헤더 또는 {}
     */
    function _getCsrfHeader() {
      const token  = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
      const header = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');
      if (token && header) return { [header]: token };
      return {};
    }

    // ── 공개 API ──────────────────────────────────────────────────────────

    /**
     * GET 요청.
     * @param {string} url
     * @returns {Promise<any>}
     */
    const get = (url) => _request('GET', url);

    /**
     * POST 요청.
     * @param {string} url
     * @param {Object} data 요청 바디
     * @returns {Promise<any>}
     */
    const post = (url, data) => _request('POST', url, data);

    /**
     * PUT 요청.
     * @param {string} url
     * @param {Object} data 요청 바디
     * @returns {Promise<any>}
     */
    const put = (url, data) => _request('PUT', url, data);

    /**
     * DELETE 요청.
     * @param {string} url
     * @returns {Promise<any>}
     */
    const del = (url) => _request('DELETE', url);

    return { get, post, put, delete: del };
  })();


  /* =====================================================================
     2. Modal 모듈 — 공용 모달 제어
     fragments/modal.html 의 #modal-container 를 조작.
     ===================================================================== */
  const modal = (() => {

    // ── DOM 요소 참조 (DOMContentLoaded 이후 초기화) ──────────────────────
    let _overlay    = null;  // #modal-container
    let _backdrop   = null;  // .modal-backdrop
    let _titleEl    = null;  // #modal-title
    let _bodyEl     = null;  // #modal-body
    let _footerEl   = null;  // #modal-footer
    let _closeBtnEl = null;  // #modal-close-btn

    // 현재 설정된 취소(배경 클릭) 핸들러 참조 (중복 방지용)
    let _onBackdropClick = null;

    /**
     * DOM 요소 초기화. 최초 1회 실행.
     */
    function _init() {
      _overlay    = document.getElementById('modal-container');
      _backdrop   = document.getElementById('modal-backdrop');
      _titleEl    = document.getElementById('modal-title');
      _bodyEl     = document.getElementById('modal-body');
      _footerEl   = document.getElementById('modal-footer');
      _closeBtnEl = document.getElementById('modal-close-btn');

      if (!_overlay) return; // modal fragment 없는 페이지에서는 무시

      // X 버튼 클릭 시 닫기
      _closeBtnEl.addEventListener('click', close);

      // ESC 키로 닫기
      document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape' && _overlay.classList.contains('is-open')) {
          close();
        }
      });
    }

    /**
     * 모달 열기.
     * @param {Object} options
     * @param {string}        options.title   모달 제목
     * @param {string}        options.content 모달 바디 HTML 문자열
     * @param {string|null}   [options.footerHtml] 푸터 버튼 HTML (null이면 빈 푸터)
     * @param {boolean}       [options.closable=true] 배경 클릭/X로 닫기 가능 여부
     */
    function open({ title, content, footerHtml = null, closable = true }) {
      if (!_overlay) return;

      // 내용 주입
      _titleEl.textContent = title;
      _bodyEl.innerHTML    = content;
      _footerEl.innerHTML  = footerHtml || '';

      // 배경 클릭 닫기 설정
      if (_onBackdropClick) {
        _backdrop.removeEventListener('click', _onBackdropClick);
      }
      if (closable) {
        _onBackdropClick = close;
        _backdrop.addEventListener('click', _onBackdropClick);
      }

      // 표시
      _overlay.classList.add('is-open');
      _overlay.setAttribute('aria-hidden', 'false');

      // 포커스 트랩: 모달 내 첫 번째 버튼으로 포커스 이동
      const firstBtn = _footerEl.querySelector('button') || _closeBtnEl;
      firstBtn?.focus();
    }

    /**
     * 모달 닫기.
     */
    function close() {
      if (!_overlay) return;
      _overlay.classList.remove('is-open');
      _overlay.setAttribute('aria-hidden', 'true');
      // 내용 초기화 (다음 열기 시 잔여 내용 방지)
      setTimeout(() => {
        if (!_overlay.classList.contains('is-open')) {
          _bodyEl.innerHTML   = '';
          _footerEl.innerHTML = '';
        }
      }, 200); // CSS transition 완료 후
    }

    /**
     * 확인/취소 2버튼 모달.
     * @param {Object} options
     * @param {string}   options.title
     * @param {string}   options.message
     * @param {string}   [options.confirmText='확인']
     * @param {string}   [options.cancelText='취소']
     * @param {Function} [options.onConfirm] 확인 버튼 클릭 콜백
     * @param {Function} [options.onCancel]  취소 버튼 클릭 콜백
     */
    function confirm({
      title,
      message,
      confirmText = '확인',
      cancelText  = '취소',
      onConfirm   = () => {},
      onCancel    = () => {},
    }) {
      // 버튼 HTML 생성
      const footerHtml = `
        <button type="button" class="btn btn--ghost" id="modal-cancel-btn">${cancelText}</button>
        <button type="button" class="btn btn--primary" id="modal-confirm-btn">${confirmText}</button>
      `;

      open({ title, content: `<p>${message}</p>`, footerHtml });

      // 버튼 이벤트 등록
      document.getElementById('modal-cancel-btn')?.addEventListener('click', () => {
        close();
        onCancel();
      });
      document.getElementById('modal-confirm-btn')?.addEventListener('click', () => {
        close();
        onConfirm();
      });
    }

    /**
     * 안내 메시지 1버튼 모달.
     * @param {Object} options
     * @param {string}   options.title
     * @param {string}   options.message
     * @param {string}   [options.closeText='확인']
     * @param {Function} [options.onClose] 버튼 클릭 콜백
     */
    function alert({ title, message, closeText = '확인', onClose = () => {} }) {
      const footerHtml = `
        <button type="button" class="btn btn--primary btn--full" id="modal-ok-btn">${closeText}</button>
      `;

      open({ title, content: `<p>${message}</p>`, footerHtml, closable: false });

      document.getElementById('modal-ok-btn')?.addEventListener('click', () => {
        close();
        onClose();
      });
    }

    // DOMContentLoaded 후 초기화
    document.addEventListener('DOMContentLoaded', _init);

    return { open, close, confirm, alert };
  })();


  /* =====================================================================
     3. Alert(Toast) 모듈 — 토스트 알림 제어
     fragments/alert.html 의 #alert-container 를 조작.
     ===================================================================== */
  const alert = (() => {

    let _container = null;

    /**
     * 타입별 SVG 아이콘 반환.
     * @param {'success'|'error'|'warning'|'info'} type
     * @returns {string} SVG HTML 문자열
     */
    function _getIcon(type) {
      const icons = {
        success: `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
                    <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/>
                  </svg>`,
        error:   `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
                    <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2
                             12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z"/>
                  </svg>`,
        warning: `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
                    <path d="M1 21h22L12 2 1 21zm12-3h-2v-2h2v2zm0-4h-2v-4h2v4z"/>
                  </svg>`,
        info:    `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
                    <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52
                             2 12 2zm1 15h-2v-6h2v6zm0-8h-2V7h2v2z"/>
                  </svg>`,
      };
      return icons[type] || icons.info;
    }

    /**
     * 토스트 알림 표시.
     * @param {string} message 알림 메시지 텍스트
     * @param {'success'|'error'|'warning'|'info'} [type='info'] 알림 타입
     * @param {number} [duration=3000] 자동 사라짐 시간 (ms). 0이면 수동 닫기만.
     */
    function show(message, type = 'info', duration = 3000) {
      if (!_container) {
        _container = document.getElementById('alert-container');
      }
      if (!_container) return;

      // 알림 요소 생성
      const el = document.createElement('div');
      el.className   = `alert alert--${type}`;
      el.setAttribute('role', 'alert');
      el.innerHTML = `
        <div class="alert__icon">${_getIcon(type)}</div>
        <span class="alert__message">${message}</span>
        <button type="button" class="alert__close-btn" aria-label="알림 닫기">×</button>
      `;

      // 닫기 버튼 이벤트
      el.querySelector('.alert__close-btn').addEventListener('click', () => dismiss(el));

      // 컨테이너에 추가
      _container.appendChild(el);

      // 자동 사라짐
      if (duration > 0) {
        setTimeout(() => dismiss(el), duration);
      }
    }

    /**
     * 특정 알림 요소 제거 (퇴장 애니메이션 후 DOM에서 삭제).
     * @param {HTMLElement} el 제거할 알림 요소
     */
    function dismiss(el) {
      if (!el || !el.parentNode) return;
      el.classList.add('alert--dismissing');
      // 애니메이션(0.25s) 완료 후 DOM에서 제거
      el.addEventListener('animationend', () => el.remove(), { once: true });
    }

    return { show, dismiss };
  })();


  /* =====================================================================
     4. Loading 모듈 — 전체 화면 로딩 스피너 제어
     ===================================================================== */
  const loading = (() => {

    let _overlay = null;

    /**
     * 스피너 오버레이 DOM 생성 및 body에 추가.
     */
    function _create() {
      _overlay = document.createElement('div');
      _overlay.className = 'spinner-overlay';
      _overlay.id        = 'loading-overlay';
      _overlay.innerHTML = `
        <div class="spinner" aria-hidden="true"></div>
        <p class="spinner-overlay__text">잠시만 기다려주세요...</p>
      `;
      document.body.appendChild(_overlay);
    }

    /**
     * 로딩 스피너 표시.
     * @param {string} [text='잠시만 기다려주세요...'] 안내 텍스트
     */
    function show(text = '잠시만 기다려주세요...') {
      if (!_overlay) _create();
      _overlay.querySelector('.spinner-overlay__text').textContent = text;
      _overlay.style.display = 'flex';
    }

    /**
     * 로딩 스피너 숨김.
     */
    function hide() {
      if (_overlay) _overlay.style.display = 'none';
    }

    return { show, hide };
  })();


  /* =====================================================================
     5. Util 모듈 — 공용 유틸리티 함수
     ===================================================================== */
  const util = (() => {

    /**
     * 숫자를 한국 원화 형식으로 포맷.
     * e.g. formatCurrency(15000) → "15,000원"
     * @param {number} amount
     * @returns {string}
     */
    function formatCurrency(amount) {
      return `${Number(amount).toLocaleString('ko-KR')}원`;
    }

    /**
     * Date 객체 또는 ISO 문자열을 "YYYY.MM.DD" 형식으로 포맷.
     * e.g. formatDate(new Date()) → "2026.03.14"
     * @param {Date|string} date
     * @returns {string}
     */
    function formatDate(date) {
      const d  = date instanceof Date ? date : new Date(date);
      const yy = d.getFullYear();
      const mm = String(d.getMonth() + 1).padStart(2, '0');
      const dd = String(d.getDate()).padStart(2, '0');
      return `${yy}.${mm}.${dd}`;
    }

    /**
     * Date 객체 또는 ISO 문자열을 "HH:MM" 형식으로 포맷.
     * e.g. formatTime(new Date()) → "14:30"
     * @param {Date|string} date
     * @returns {string}
     */
    function formatTime(date) {
      const d  = date instanceof Date ? date : new Date(date);
      const hh = String(d.getHours()).padStart(2, '0');
      const mm = String(d.getMinutes()).padStart(2, '0');
      return `${hh}:${mm}`;
    }

    /**
     * 휴대폰 번호 유효성 검사.
     * 010/011/016/017/018/019 로 시작하는 10~11자리 숫자.
     * @param {string} phone
     * @returns {boolean}
     */
    function isValidPhone(phone) {
      return /^01[016789]\d{7,8}$/.test(phone.replace(/-/g, ''));
    }

    /**
     * 문자열이 비어 있는지 확인 (null, undefined, 공백 포함).
     * @param {string|null|undefined} str
     * @returns {boolean}
     */
    function isEmpty(str) {
      return str == null || String(str).trim() === '';
    }

    /**
     * 예매 번호(booking number) 포맷. 임시 플레이스홀더.
     * 실제 포맷은 백엔드 정책 확정 후 업데이트 필요.
     * @param {string|number} id
     * @returns {string}
     */
    function formatBookingNo(id) {
      return `B${String(id).padStart(8, '0')}`;
    }

    /**
     * 남은 시간을 "MM:SS" 형식으로 포맷 (결제 타이머 용).
     * e.g. formatCountdown(125) → "02:05"
     * @param {number} seconds 남은 초
     * @returns {string}
     */
    function formatCountdown(seconds) {
      const m = Math.floor(seconds / 60);
      const s = seconds % 60;
      return `${String(m).padStart(2, '0')}:${String(s).padStart(2, '0')}`;
    }

    return {
      formatCurrency,
      formatDate,
      formatTime,
      isValidPhone,
      isEmpty,
      formatBookingNo,
      formatCountdown,
    };
  })();


  /* =====================================================================
     6. 전역 에러 핸들러 등록
     catch되지 않은 Promise reject를 토스트 알림으로 표시.
     ===================================================================== */
  window.addEventListener('unhandledrejection', (event) => {
    // 개발 환경에서는 콘솔에도 출력
    console.error('[CineOS] Unhandled Promise rejection:', event.reason);

    // 사용자에게는 일반적인 오류 메시지 표시
    const message = event.reason?.message || '오류가 발생하였습니다. 다시 시도해 주세요.';
    alert.show(message, 'error');
  });


  // ── 공개 네임스페이스 반환 ──────────────────────────────────────────────
  return {
    api,
    modal,
    alert,
    loading,
    util,
  };

})();
