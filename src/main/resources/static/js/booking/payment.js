/**
 * static/js/booking/payment.js
 * ─────────────────────────────────────────────────────────────────────────
 * UC-04: 결제 처리 / UC-05: 휴대폰 인증 / UC-06: 포인트 사용
 *
 * ▶ 페이지 진입 시 자동 모달 흐름
 *   Step 1 — 회원 여부 확인
 *     └ 예     → Step 2
 *     └ 비회원 → Step 3
 *   Step 2 — 휴대폰 번호 입력 + 인증 (POST /api/auth/phone)
 *     └ 성공   → 포인트 섹션 활성화 → Step 3
 *     └ 건너뛰기 → Step 3
 *   Step 3 — 쿠폰 코드 입력 (회원/비회원 공통)
 *     └ 적용   → 쿠폰 섹션 표시 + 금액 반영 (TODO: API 연동)
 *     └ 건너뛰기 → 모달 닫기
 *
 *   ※ MEMBER_PHONE이 이미 있으면 (서버 세션 복원):
 *      Step 1~2 생략 → 포인트 섹션 즉시 활성화 → Step 3 자동 오픈
 *
 * ▶ 담당 기능
 *   1. 임시 점유 타이머 1분 카운트다운 (조작 시 리셋, 만료 → /)
 *   2. 자동 모달 흐름 (회원 확인 → 인증 → 쿠폰)
 *   3. 포인트 사용 UI (인증 완료 후 페이지 인라인 입력)
 *   4. 쿠폰 적용 UI + 가격 반영 (API TODO)
 *   5. 결제 금액 실시간 계산 (기본 요금 - 쿠폰 - 포인트)
 *   6. 결제 수단 선택 → 클릭 즉시 submitPayment(method) 호출
 *      (현금 제외, CARD / SIMPLE만 지원)
 *   7. 결제 요청 (UC-04): POST /api/payment → /payment/result 이동
 *
 *   ※ 할인 정책 선택은 UC-03 이전 단계에서 이미 처리되므로
 *      결제 단계에서는 관여하지 않음.
 *   ※ '결제하기' 버튼 없음 — 카드/간편 클릭 시 즉시 결제.
 *      0원이면 #btn-free-pay 버튼으로 트리거.
 *
 * ▶ 의존 전역 변수 (payment.html th:inline에서 주입)
 *   RESERVATION_ID, SCHEDULE_ID, SEAT_NUMBERS, SEAT_COST,
 *   ADULT_COUNT, TEEN_COUNT, BONUS_POLICIES,
 *   MEMBER_PHONE, MEMBER_POINT
 *
 * ▶ 의존 전역 객체
 *   CineOS.api   - REST API 래퍼 (common.js)
 *   CineOS.modal - 공용 모달: open({title, content, footerHtml, closable})
 *   CineOS.alert - 토스트 알림 (common.js)
 *
 * ▶ 연결 template
 *   templates/payment/payment.html
 * ─────────────────────────────────────────────────────────────────────────
 */

'use strict';

/* ══════════════════════════════════════════════════════════════════
   상수
══════════════════════════════════════════════════════════════════ */


/** 청소년 요금 비율: 성인 SEAT_COST 의 80% */
const TEEN_COST_RATIO = 0.8;

/**
 * 결제 수단 코드 (POST /api/payment 의 paymentMethod 필드값)
 * 현금(CASH)은 이번 키오스크 범위에서 제외.
 * @enum {string}
 */
const PAY_METHOD = {
  CARD:   'CARD',
  SIMPLE: 'SIMPLE',
};

/**
 * 개발용 전화번호 인증 mock 활성화 플래그.
 * true: 실제 API 호출 없이 임의의 번호를 인증 성공으로 처리.
 *       백엔드 /api/auth/phone 구현 전 UI 흐름 테스트용.
 * false: 실제 POST /api/auth/phone + GET /api/members/point 호출.
 *
 * TODO: 백엔드 연동 완료 후 false로 변경 후 이 상수 제거.
 * @type {boolean}
 */
const DEV_MOCK_PHONE_AUTH = true;

/**
 * DEV_MOCK_PHONE_AUTH 가 true일 때 사용할 목 포인트 잔액.
 * 실제 회원 데이터 없이 포인트 UI를 확인하기 위한 임시값.
 * @type {number}
 */
const DEV_MOCK_POINT_BALANCE = 5000;


/* ══════════════════════════════════════════════════════════════════
   페이지 전역 상태
══════════════════════════════════════════════════════════════════ */

/**
 * 결제 화면 상태 객체
 * @type {{
 *   usePoint: number,
 *   memberPhone: string|null,
 *   memberPointBalance: number,
 *   couponCode: string|null,
 *   couponDiscount: number,
 *   basePrice: number,
 * }}
 */
const state = {
  usePoint:            0,
  memberPhone:         MEMBER_PHONE,       // 서버 세션에서 주입, 없으면 null
  memberPointBalance:  MEMBER_POINT,       // 서버 세션에서 주입, 없으면 0
  couponCode:          null,               // 적용된 쿠폰 코드 (없으면 null)
  couponDiscount:      0,                  // 쿠폰 할인 금액 (원)
  basePrice:           0,
};


/* ══════════════════════════════════════════════════════════════════
   유틸리티 함수
══════════════════════════════════════════════════════════════════ */

/**
 * 숫자를 한국 통화 형식으로 변환.
 * @param {number} amount
 * @returns {string} "15,000원"
 */
function formatCurrency(amount) {
  return amount.toLocaleString('ko-KR') + '원';
}

/**
 * 숫자를 포인트 형식으로 변환.
 * @param {number} point
 * @returns {string} "1,500P"
 */
function formatPoint(point) {
  return point.toLocaleString('ko-KR') + 'P';
}

/**
 * ISO 8601 날짜 문자열을 "MM/DD (요일) HH:MM ~ HH:MM" 형식으로 변환.
 * @param {string} startIso  예: "2026-03-20T14:00"
 * @param {string} [endIso]  예: "2026-03-20T16:30"
 * @returns {string}
 */
function formatDatetime(startIso, endIso) {
  if (!startIso) return '-';

  const DAY_LABELS = ['일', '월', '화', '수', '목', '금', '토'];
  const start = new Date(startIso);
  const mm  = String(start.getMonth() + 1).padStart(2, '0');
  const dd  = String(start.getDate()).padStart(2, '0');
  const dow = DAY_LABELS[start.getDay()];
  const sh  = String(start.getHours()).padStart(2, '0');
  const sm  = String(start.getMinutes()).padStart(2, '0');

  let result = `${mm}/${dd} (${dow}) ${sh}:${sm}`;

  if (endIso) {
    const end = new Date(endIso);
    const eh  = String(end.getHours()).padStart(2, '0');
    const em  = String(end.getMinutes()).padStart(2, '0');
    result += ` ~ ${eh}:${em}`;
  }

  return result;
}


/* ══════════════════════════════════════════════════════════════════
   타이머
   ─────────────────────────────────────────────────────────────────
   임시 점유 타이머는 idle-timer.js (base.html 공통 로드) 로 위임.
   결제 페이지도 idleTimer=true 로 footer 뱃지 + 경고 오버레이 공통 사용.
   별도 타이머 UI(진행 바 등)는 제거함.
══════════════════════════════════════════════════════════════════ */


/* ══════════════════════════════════════════════════════════════════
   기본 요금 계산
══════════════════════════════════════════════════════════════════ */

/**
 * 기본 요금 계산.
 * 성인: SEAT_COST × ADULT_COUNT
 * 청소년: Math.floor(SEAT_COST × TEEN_COST_RATIO) × TEEN_COUNT
 * @returns {number}
 */
function calcBasePrice() {
  const adultPrice = SEAT_COST * ADULT_COUNT;
  const teenPrice  = Math.floor(SEAT_COST * TEEN_COST_RATIO) * TEEN_COUNT;
  return adultPrice + teenPrice;
}


/* ══════════════════════════════════════════════════════════════════
   가격 요약 UI 업데이트 (실시간)
══════════════════════════════════════════════════════════════════ */

/**
 * 가격 요약 섹션 전체 업데이트.
 * 쿠폰 적용·포인트 입력 때마다 호출.
 * 최종 금액 = Math.max(0, basePrice - couponDiscount - usePoint)
 *
 * 총 결제 금액이 0원이면:
 *   - #pay-method-wrap 숨김
 *   - #free-pay-wrap 표시
 * 0원이 아니면:
 *   - #pay-method-wrap 표시
 *   - #free-pay-wrap 숨김
 */
function updatePriceUI() {
  const base   = state.basePrice;

  /* 쿠폰 할인: basePrice 초과 불가 */
  const coupon = Math.min(state.couponDiscount, base);

  /* 포인트: (기본 요금 - 쿠폰) 이하로 제한 */
  const maxAfterCoupon = Math.max(0, base - coupon);
  const point  = Math.min(state.usePoint, maxAfterCoupon);

  const total  = Math.max(0, base - coupon - point);

  /* ── 기본 요금 ── */
  document.getElementById('price-base').textContent = formatCurrency(base);

  /* ── 쿠폰 할인 행 (쿠폰 적용 시에만 표시) ── */
  const couponRow = document.getElementById('price-coupon-row');
  if (coupon > 0) {
    couponRow.hidden = false;
    document.getElementById('price-coupon').textContent = `-${formatCurrency(coupon)}`;
  } else {
    couponRow.hidden = true;
  }

  /* ── 포인트 사용 행 (포인트 사용 시에만 표시) ── */
  const pointRow = document.getElementById('price-point-row');
  if (point > 0) {
    pointRow.hidden = false;
    document.getElementById('price-point').textContent = `-${formatPoint(point)}`;
  } else {
    pointRow.hidden = true;
  }

  /* ── 총 결제 금액 ── */
  document.getElementById('price-total').textContent = formatCurrency(total);

  /* ── 포인트 적립 예정 안내 ── */
  updateBonusNote(total);

  /* ── 결제 수단 / 0원 결제 버튼 전환 ── */
  const payMethodWrap = document.getElementById('pay-method-wrap');
  const freePayWrap   = document.getElementById('free-pay-wrap');

  if (total === 0) {
    /* 포인트·쿠폰으로 전액 처리: 카드/간편 버튼 숨기고 무료결제 버튼 표시 */
    if (payMethodWrap) payMethodWrap.hidden = true;
    if (freePayWrap)   freePayWrap.hidden   = false;
  } else {
    if (payMethodWrap) payMethodWrap.hidden = false;
    if (freePayWrap)   freePayWrap.hidden   = true;
  }
}

/**
 * 포인트 적립 예정 안내 문구 업데이트.
 * @param {number} finalAmount 최종 결제 금액
 */
function updateBonusNote(finalAmount) {
  const noteEl = document.getElementById('price-bonus-note');
  if (!noteEl) return;

  if (!BONUS_POLICIES || BONUS_POLICIES.length === 0 || finalAmount === 0) {
    noteEl.hidden = true;
    return;
  }

  const policy    = BONUS_POLICIES[0];
  const earnPoint = Math.floor(finalAmount * (policy.giveValue / 100));

  if (earnPoint > 0) {
    noteEl.hidden = false;
    document.getElementById('price-bonus-amount').textContent = formatPoint(earnPoint);
  } else {
    noteEl.hidden = true;
  }
}


/* ══════════════════════════════════════════════════════════════════
   자동 모달 흐름 진입점
══════════════════════════════════════════════════════════════════ */

/**
 * 페이지 초기화 후 자동 실행되는 모달 흐름 시작.
 * - 이미 인증된 경우 (MEMBER_PHONE 있음): 포인트 UI 활성화 → 쿠폰 Step만 오픈
 * - 미인증: Step 1 (회원 여부) 부터 오픈
 */
function startInitialModal() {
  if (state.memberPhone !== null) {
    /* 서버 세션에 이미 member 있음: 포인트 UI 즉시 활성화 */
    activatePointSection(state.memberPointBalance);
    /* 쿠폰 단계만 오픈 */
    openModalStep3Coupon();
  } else {
    /* 회원 여부부터 확인 */
    openModalStep1Member();
  }
}


/* ══════════════════════════════════════════════════════════════════
   Step 1: 회원 여부 확인 모달
══════════════════════════════════════════════════════════════════ */

/**
 * Step 1: 회원/비회원 선택.
 * 예 → openModalStep2Phone()
 * 비회원 → openModalStep3Coupon()
 */
function openModalStep1Member() {
  const template = document.getElementById('modal-step1-member');
  if (!template || typeof CineOS === 'undefined' || !CineOS.modal) return;

  const content = template.content.cloneNode(true).firstElementChild.outerHTML;

  /* closable: false — X 버튼·배경 클릭으로 닫지 못하게.
     스텝 내부 버튼으로만 진행 가능 */
  CineOS.modal.open({ title: '회원 확인', content, closable: false });

  /* open()은 동기적으로 content를 #modal-body에 주입 → 바로 이벤트 등록 */
  const btnYes = document.querySelector('#modal-body #modal-btn-yes');
  const btnNo  = document.querySelector('#modal-body #modal-btn-no');

  if (btnYes) {
    btnYes.addEventListener('click', () => {
      /* 회원 → 전화번호 인증 단계 */
      openModalStep2Phone();
    });
  }
  if (btnNo) {
    btnNo.addEventListener('click', () => {
      /* 비회원 → 쿠폰 단계로 바로 이동 */
      openModalStep3Coupon();
    });
  }
}


/* ══════════════════════════════════════════════════════════════════
   Step 2: 휴대폰 번호 입력 + 인증 모달
══════════════════════════════════════════════════════════════════ */

/**
 * Step 2: 휴대폰 번호 입력 + 인증.
 * 성공 → 포인트 UI 활성화 → openModalStep3Coupon()
 * 건너뛰기 → openModalStep3Coupon()
 */
function openModalStep2Phone() {
  const template = document.getElementById('modal-step2-phone');
  if (!template || typeof CineOS === 'undefined' || !CineOS.modal) return;

  const content = template.content.cloneNode(true).firstElementChild.outerHTML;

  CineOS.modal.open({ title: '휴대폰 인증', content, closable: false });

  const phoneInput  = document.querySelector('#modal-body #modal-phone-input');
  const btnVerify   = document.querySelector('#modal-body #modal-btn-verify');
  const btnSkipAuth = document.querySelector('#modal-body #modal-btn-skip-auth');

  /* 인증하기 버튼 */
  if (btnVerify) {
    btnVerify.addEventListener('click', () => {
      const phone = phoneInput ? phoneInput.value.trim() : '';
      submitPhoneVerify(phone);
    });
  }

  /* Enter 키로도 인증 가능 */
  if (phoneInput) {
    phoneInput.addEventListener('keydown', (e) => {
      if (e.key === 'Enter') submitPhoneVerify(phoneInput.value.trim());
    });
  }

  /* 건너뛰기: 인증 없이 쿠폰 단계로 */
  if (btnSkipAuth) {
    btnSkipAuth.addEventListener('click', () => {
      openModalStep3Coupon();
    });
  }
}

/**
 * 휴대폰 인증 처리 (UC-05).
 *
 * DEV_MOCK_PHONE_AUTH = true 일 때:
 *   실제 API 호출 없이 mock 포인트로 인증 성공 처리.
 *   백엔드 /api/auth/phone 미구현 상태에서 UI 흐름 테스트 가능.
 *
 * DEV_MOCK_PHONE_AUTH = false 일 때:
 *   POST /api/auth/phone → 성공 시 GET /api/members/point 순차 호출.
 *
 * @param {string} phone 입력된 휴대폰 번호
 */
async function submitPhoneVerify(phone) {
  /* 형식 검증: 01X-XXXX-XXXX (하이픈 없이 10~11자리 숫자) */
  const phoneRegex = /^01[0-9]{8,9}$/;
  const errorEl    = document.querySelector('#modal-body #modal-phone-error');

  if (!phone || !phoneRegex.test(phone)) {
    /* UC-05 명세 에러 메시지 (임의 변경 금지) */
    if (errorEl) {
      errorEl.hidden      = false;
      errorEl.textContent = '올바른 휴대폰 번호 형식으로 입력해 주세요.';
    }
    return;
  }

  if (errorEl) {
    errorEl.hidden      = true;
    errorEl.textContent = '';
  }

  /* ── 개발용 mock ────────────────────────────────────────────────
     TODO: 백엔드 연동 완료 후 DEV_MOCK_PHONE_AUTH = false 로 변경.
     ────────────────────────────────────────────────────────────── */
  if (DEV_MOCK_PHONE_AUTH) {
    console.warn('[DEV] 전화번호 인증 mock 활성화 — 실제 API 미호출');

    /* 상태 갱신 (mock 포인트 사용) */
    state.memberPhone        = phone;
    state.memberPointBalance = DEV_MOCK_POINT_BALANCE;

    activatePointSection(DEV_MOCK_POINT_BALANCE);
    openModalStep3Coupon();
    return;
  }

  /* ── 실제 API 호출 ────────────────────────────────────────────── */
  try {
    /* POST /api/auth/phone — 회원 조회 및 인증 */
    // TODO: 백엔드 AuthController 구현 후 응답 형식 확인
    await CineOS.api.post('/api/auth/phone', { phone });

    /* GET /api/members/point?phone= — 포인트 잔액 조회 */
    // TODO: 백엔드 MemberController 구현 후 응답 형식 확인
    const pointData    = await CineOS.api.get(`/api/members/point?phone=${encodeURIComponent(phone)}`);
    const pointBalance = (pointData && typeof pointData.point === 'number')
      ? pointData.point
      : 0;

    /* 상태 갱신 */
    state.memberPhone        = phone;
    state.memberPointBalance = pointBalance;

    /* 페이지 내 포인트 섹션 활성화 */
    activatePointSection(pointBalance);

    /* 인증 완료 후 쿠폰 단계로 이동 */
    openModalStep3Coupon();

  } catch (err) {
    /* UC-05 명세 에러 메시지 (임의 변경 금지) */
    const status  = err?.status ?? err?.response?.status;
    const message = status === 404
      ? '등록되지 않은 번호입니다.'
      : '인증 중 오류가 발생했습니다. 다시 시도해 주세요.';

    if (errorEl) {
      errorEl.hidden      = false;
      errorEl.textContent = message;
    }
  }
}


/* ══════════════════════════════════════════════════════════════════
   Step 3: 쿠폰 코드 입력 모달 (회원/비회원 공통)
══════════════════════════════════════════════════════════════════ */

/**
 * Step 3: 쿠폰 코드 입력.
 * 적용     → 쿠폰 상태 반영 + 페이지 쿠폰 섹션 표시 → 모달 닫기
 * 건너뛰기 → 모달 닫기
 */
function openModalStep3Coupon() {
  const template = document.getElementById('modal-step3-coupon');
  if (!template || typeof CineOS === 'undefined' || !CineOS.modal) return;

  const content = template.content.cloneNode(true).firstElementChild.outerHTML;

  /* closable: true — 쿠폰은 선택 사항이므로 X·배경 클릭으로 닫기 허용 */
  CineOS.modal.open({ title: '쿠폰 적용', content, closable: true });

  const couponInput = document.querySelector('#modal-body #modal-coupon-input');
  const btnApply    = document.querySelector('#modal-body #modal-btn-coupon-apply');
  const btnSkip     = document.querySelector('#modal-body #modal-btn-coupon-skip');
  const errorEl     = document.querySelector('#modal-body #modal-coupon-error');
  const successEl   = document.querySelector('#modal-body #modal-coupon-success');

  /* 이전에 적용된 쿠폰 코드가 있으면 입력 필드에 복원 */
  if (couponInput && state.couponCode) {
    couponInput.value = state.couponCode;
  }

  /* 적용 버튼 */
  if (btnApply) {
    btnApply.addEventListener('click', () => {
      const code = couponInput ? couponInput.value.trim().toUpperCase() : '';
      submitCoupon(code, errorEl, successEl);
    });
  }

  /* Enter 키로도 적용 가능 */
  if (couponInput) {
    couponInput.addEventListener('keydown', (e) => {
      if (e.key === 'Enter') {
        submitCoupon(couponInput.value.trim().toUpperCase(), errorEl, successEl);
      }
    });
  }

  /* 건너뛰기 버튼 */
  if (btnSkip) {
    btnSkip.addEventListener('click', () => {
      CineOS.modal.close();
    });
  }
}

/**
 * 쿠폰 코드 적용 처리.
 * TODO: 백엔드 API 연동 후 실제 검증 로직으로 교체.
 *       현재는 UI 흐름만 구현.
 *
 * @param {string}           code       입력된 쿠폰 코드 (대문자 변환됨)
 * @param {HTMLElement|null} errorEl    에러 메시지 표시 요소
 * @param {HTMLElement|null} successEl  성공 메시지 표시 요소
 */
async function submitCoupon(code, errorEl, successEl) {
  if (!code) {
    if (errorEl) {
      errorEl.hidden      = false;
      errorEl.textContent = '쿠폰 코드를 입력해 주세요.';
    }
    return;
  }

  /* 에러/성공 메시지 초기화 */
  if (errorEl)   { errorEl.hidden   = true; errorEl.textContent   = ''; }
  if (successEl) { successEl.hidden = true; successEl.textContent = ''; }

  try {
    /**
     * TODO: POST /api/coupon/validate — 쿠폰 유효성 검증 + 할인 금액 조회
     * 응답 예시: { couponCode: "SUMMER10", discountAmount: 3000 }
     * 백엔드 CouponController 구현 후 아래 임시 로직을 교체할 것.
     *
     * const result = await CineOS.api.post('/api/coupon/validate', {
     *   couponCode:    code,
     *   reservationId: RESERVATION_ID,
     * });
     * const discountAmount = result.discountAmount;
     */

    /* ── 임시: API 없이 UI 흐름만 시연 ── */
    const discountAmount = 0; // TODO: API 응답값으로 교체
    /* ──────────────────────────────── */

    /* 상태 반영 */
    state.couponCode     = code;
    state.couponDiscount = discountAmount;

    /* 성공 메시지 표시 */
    if (successEl) {
      successEl.hidden      = false;
      successEl.textContent = '쿠폰이 적용되었습니다.';
    }

    /* 페이지 쿠폰 섹션 표시 & 결제 금액 갱신 */
    activateCouponSection(code, discountAmount);
    updatePriceUI();

    /* 잠깐 보여준 후 모달 닫기 */
    setTimeout(() => CineOS.modal.close(), 800);

  } catch (err) {
    const status  = err?.status ?? err?.response?.status;
    const message = status === 404
      ? '유효하지 않은 쿠폰 코드입니다.'
      : '쿠폰 적용 중 오류가 발생했습니다. 다시 시도해 주세요.';

    if (errorEl) {
      errorEl.hidden      = false;
      errorEl.textContent = message;
    }
  }
}


/* ══════════════════════════════════════════════════════════════════
   포인트 섹션 활성화 (페이지 인라인 UI)
══════════════════════════════════════════════════════════════════ */

/**
 * 회원 인증 완료 후 페이지 내 포인트 섹션 표시.
 * #point-section hidden 해제 + 잔액 표시.
 * @param {number} pointBalance 보유 포인트
 */
function activatePointSection(pointBalance) {
  const sectionEl = document.getElementById('point-section');
  const balanceEl = document.getElementById('point-balance-display');

  if (sectionEl) sectionEl.hidden = false;
  if (balanceEl) balanceEl.textContent = formatPoint(pointBalance);
}


/* ══════════════════════════════════════════════════════════════════
   쿠폰 섹션 활성화 (페이지 인라인 UI)
══════════════════════════════════════════════════════════════════ */

/**
 * 쿠폰 적용 완료 후 페이지 내 쿠폰 섹션 표시.
 * #coupon-section hidden 해제 + 쿠폰 코드·할인 금액 표시.
 * @param {string} code           쿠폰 코드
 * @param {number} discountAmount 할인 금액 (원)
 */
function activateCouponSection(code, discountAmount) {
  const sectionEl  = document.getElementById('coupon-section');
  const nameEl     = document.getElementById('coupon-applied-name');
  const discountEl = document.getElementById('coupon-applied-discount');

  if (sectionEl) sectionEl.hidden = false;
  if (nameEl)    nameEl.textContent    = code;
  if (discountEl) discountEl.textContent = discountAmount > 0
    ? `-${formatCurrency(discountAmount)}`
    : '적용됨';  /* discountAmount가 0이면 "적용됨"으로 표시 (API 연동 전 임시) */
}

/**
 * 쿠폰 취소 처리.
 * 쿠폰 섹션 숨기기 + 상태 초기화 + 가격 갱신.
 */
function removeCoupon() {
  state.couponCode     = null;
  state.couponDiscount = 0;

  const sectionEl = document.getElementById('coupon-section');
  if (sectionEl) sectionEl.hidden = true;

  updatePriceUI();
}


/* ══════════════════════════════════════════════════════════════════
   포인트 직접 입력 (페이지 인라인 — 인증 완료 후)
══════════════════════════════════════════════════════════════════ */

/**
 * 포인트 입력 + 전액 사용 이벤트 초기화.
 * 0 ~ min(보유포인트, 기본요금 - 쿠폰) 범위로 제한.
 */
function initPointInput() {
  const pointInput  = document.getElementById('point-input');
  const btnPointAll = document.getElementById('btn-point-all');
  const pointError  = document.getElementById('point-error');

  if (!pointInput) return;

  /* 직접 입력 이벤트 */
  pointInput.addEventListener('input', () => {
    const val       = parseInt(pointInput.value, 10) || 0;
    const maxUsable = Math.max(0, state.basePrice - state.couponDiscount);
    const maxPoint  = Math.min(state.memberPointBalance, maxUsable);

    if (val > maxPoint) {
      pointInput.value = maxPoint;
      state.usePoint   = maxPoint;

      if (pointError) {
        pointError.hidden      = false;
        pointError.textContent = `최대 ${formatPoint(maxPoint)}까지 사용 가능합니다.`;
      }
    } else {
      state.usePoint = Math.max(0, val);
      if (pointError) pointError.hidden = true;
    }

    updatePriceUI();
  });

  /* 전액 사용 버튼 */
  if (btnPointAll) {
    btnPointAll.addEventListener('click', () => {
      const maxUsable = Math.max(0, state.basePrice - state.couponDiscount);
      const useAll    = Math.min(state.memberPointBalance, maxUsable);

      pointInput.value = useAll;
      state.usePoint   = useAll;

      if (pointError) pointError.hidden = true;
      updatePriceUI();
    });
  }
}


/* ══════════════════════════════════════════════════════════════════
   결제 수단 버튼 — 클릭 즉시 결제
══════════════════════════════════════════════════════════════════ */

/**
 * 결제 수단 버튼 클릭 이벤트 초기화.
 * 기존의 '선택 → 결제하기 버튼' 2단계 흐름을 제거하고
 * 버튼 클릭 즉시 submitPayment(method) 를 호출한다.
 *
 * 현금(CASH)은 HTML에서 이미 제거됐으므로 CARD / SIMPLE 만 등록됨.
 */
function initPayMethodButtons() {
  const buttons = document.querySelectorAll('.pay-method-btn');

  buttons.forEach(btn => {
    btn.addEventListener('click', () => {
      const method = btn.dataset.method;

      /* 선택 시각 피드백 (잠깐 is-selected 표시 — 바로 결제로 넘어가므로 사용자 경험용) */
      buttons.forEach(b => {
        b.classList.remove('is-selected');
        b.setAttribute('aria-pressed', 'false');
      });
      btn.classList.add('is-selected');
      btn.setAttribute('aria-pressed', 'true');

      /* 클릭 즉시 결제 진행 */
      submitPayment(method);
    });
  });
}


/* ══════════════════════════════════════════════════════════════════
   결제 요청 (UC-04)
══════════════════════════════════════════════════════════════════ */

/**
 * 결제 요청.
 * POST /api/payment
 * 성공: /payment/result?reservationId= 이동
 * 실패: "결제에 실패하였습니다. 다시 시도해 주세요." → 버튼 복원
 *
 * @param {string|null} method 결제 수단 코드 (PAY_METHOD).
 *   0원 결제(전액 포인트/쿠폰)인 경우 null 전달.
 */
async function submitPayment(method) {
  const base   = state.basePrice;
  const coupon = Math.min(state.couponDiscount, base);
  const point  = Math.min(state.usePoint, Math.max(0, base - coupon));
  const total  = Math.max(0, base - coupon - point);

  /* 0원이 아닌데 수단이 없으면 리턴 (방어 코드) */
  if (total > 0 && !method) return;

  /* ── 결제 수단 버튼 전체 비활성화 (중복 클릭 방지) ── */
  const allPayBtns = document.querySelectorAll('.pay-method-btn, #btn-free-pay');
  allPayBtns.forEach(b => { b.disabled = true; });

  if (typeof CineOS !== 'undefined' && CineOS.loading) CineOS.loading.show();

  /**
   * 결제 요청 DTO
   * @see KiOsk 명세서 - API 고객 영역.csv
   * @type {{
   *   reservationId: number,
   *   usePoint: number,
   *   couponCode: string|null,
   *   paymentMethod: string|null,
   * }}
   */
  const payload = {
    reservationId: RESERVATION_ID,
    usePoint:      point,
    couponCode:    state.couponCode ?? null,    // TODO: 쿠폰 API 연동 후 활성화
    paymentMethod: total === 0 ? null : method,
  };

  try {
    /* POST /api/payment */
    // TODO: 백엔드 PaymentController 구현 후 엔드포인트·응답 형식 확인
    await CineOS.api.post('/api/payment', payload);

    /* stopTimer() 제거 — idle-timer.js 로 위임, 페이지 이동 시 자동 소멸 */
    if (typeof CineOS !== 'undefined' && CineOS.loading) CineOS.loading.hide();

    /* UC-07 결제 완료 화면으로 이동 */
    window.location.href =
      `/payment/result?reservationId=${encodeURIComponent(RESERVATION_ID)}`;

  } catch (err) {
    if (typeof CineOS !== 'undefined' && CineOS.loading) CineOS.loading.hide();

    /* 버튼 다시 활성화 (재시도 가능) */
    allPayBtns.forEach(b => { b.disabled = false; });

    /* UC-04 명세 에러 메시지 (임의 변경 금지) */
    if (typeof CineOS !== 'undefined' && CineOS.modal) {
      CineOS.modal.alert({
        title:   '결제 실패',
        message: '결제에 실패하였습니다. 다시 시도해 주세요.',
      });
    } else {
      alert('결제에 실패하였습니다. 다시 시도해 주세요.');
    }

    /* 결제 수단 선택 상태 초기화 (다시 선택하도록) */
    document.querySelectorAll('.pay-method-btn').forEach(b => {
      b.classList.remove('is-selected');
      b.setAttribute('aria-pressed', 'false');
    });
  }
}


/* ══════════════════════════════════════════════════════════════════
   예매 정보 초기화 (SSR 보완)
══════════════════════════════════════════════════════════════════ */

/**
 * booking-datetime 요소의 data-start / data-end 속성값을
 * 사람이 읽기 좋은 형식으로 변환.
 */
function initBookingDatetime() {
  const el = document.getElementById('booking-datetime');
  if (!el) return;
  el.textContent = formatDatetime(el.dataset.start || '', el.dataset.end || '');
}

/**
 * SSR로 좌석 번호가 없을 경우 JS 상수 SEAT_NUMBERS로 보완.
 */
function initSeatsDisplay() {
  const seatsEl = document.getElementById('booking-seats');
  if (!seatsEl) return;

  const currentText = seatsEl.textContent.trim();
  if (currentText !== '-' && currentText !== '') return;
  if (!SEAT_NUMBERS || SEAT_NUMBERS.length === 0) return;

  seatsEl.textContent = Array.isArray(SEAT_NUMBERS)
    ? SEAT_NUMBERS.join(', ')
    : String(SEAT_NUMBERS);
}


/* ══════════════════════════════════════════════════════════════════
   페이지 초기화
══════════════════════════════════════════════════════════════════ */

/**
 * DOMContentLoaded 이후 전체 결제 페이지 초기화.
 * 실행 순서:
 *   1. 기본 요금 계산 → state.basePrice 설정
 *   2. 가격 UI 초기 렌더링
 *   3. 포인트 입력 이벤트 등록
 *   4. 결제 수단 버튼 이벤트 등록
 *   5. 예매 정보 날짜 포맷 변환
 *   6. 좌석 번호 보완 (SSR 값 없을 때 JS 상수로 대체)
 *   7. 자동 모달 흐름 시작 (300ms 지연 — 화면 렌더 완료 후 오픈)
 *   8. 재오픈 버튼 + 0원 결제 버튼 이벤트 등록
 */
document.addEventListener('DOMContentLoaded', () => {

  /* ── 1. 기본 요금 계산 ────────────────────────────────────────── */
  state.basePrice = calcBasePrice();

  /* ── 2. 가격 UI 초기 렌더링 ──────────────────────────────────── */
  updatePriceUI();

  /* ── 3. 포인트 입력 이벤트 등록 ──────────────────────────────── */
  initPointInput();

  /* ── 4. 결제 수단 버튼 이벤트 등록 ──────────────────────────── */
  initPayMethodButtons();

  /* ── 5. 예매 정보 날짜 포맷 변환 (SSR 보완) ───────────────────── */
  initBookingDatetime();

  /* ── 6. 좌석 번호 보완 (SSR '-' 인 경우 JS 상수로 대체) ─────── */
  initSeatsDisplay();

  /* ── 7. 자동 모달 흐름 시작 ──────────────────────────────────── */
  /* 300ms 지연: DOM 렌더링 + idle-timer.js 초기화 완료 후 모달 오픈 */
  setTimeout(startInitialModal, 300);

  /* ── 8-a. 재오픈 버튼: 회원 정보 / 쿠폰 변경 ───────────────── */
  const btnReopen = document.getElementById('btn-reopen-modal');
  if (btnReopen) {
    btnReopen.addEventListener('click', () => {
      /* 인증 완료 여부에 따라 진입 단계 결정 */
      if (state.memberPhone !== null) {
        /* 이미 인증됨 → 쿠폰 단계만 재오픈 */
        openModalStep3Coupon();
      } else {
        /* 미인증 → Step 1 부터 다시 */
        openModalStep1Member();
      }
    });
  }

  /* ── 8-b. 0원 결제 버튼 (포인트·쿠폰 전액 적용 시) ─────────── */
  const btnFreePay = document.getElementById('btn-free-pay');
  if (btnFreePay) {
    btnFreePay.addEventListener('click', () => {
      /* method=null → submitPayment 내부에서 0원 결제로 처리 */
      submitPayment(null);
    });
  }
});