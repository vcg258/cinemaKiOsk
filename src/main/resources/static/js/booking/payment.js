/**
 * static/js/booking/payment.js
 * ─────────────────────────────────────────────────────────────────────────
 * UC-04: 결제 처리 / UC-05: 휴대폰 인증 / UC-06: 포인트 사용
 *
 * ▶ 담당 기능
 *   1. 임시 점유 타이머 10분 카운트다운 (만료 → UC-03 재시작)
 *   2. 할인 정책 선택 & 금액 실시간 계산
 *   3. 포인트 인증 모달 (UC-05): CineOS.modal + phoneVerifyModal fragment
 *   4. 포인트 사용 (UC-06): 직접 입력 / 전액 사용
 *   5. 결제 수단 선택
 *   6. 결제 요청 (UC-04): POST /api/payment → /payment/result 이동
 *
 * ▶ 의존 전역 변수 (payment.html th:inline에서 주입)
 *   RESERVATION_ID, SCHEDULE_ID, SEAT_NUMBERS, SEAT_COST,
 *   ADULT_COUNT, TEEN_COUNT, DISCOUNT_POLICIES, BONUS_POLICIES,
 *   MEMBER_PHONE, MEMBER_POINT
 *
 * ▶ 의존 전역 객체
 *   CineOS.api   - REST API 래퍼 (common.js)
 *   CineOS.modal - 공용 모달 (common.js)
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

/** 임시 좌석 점유 제한: 10분 (600초) */
const TIMER_TOTAL_SEC = 600;

/** 긴급 임박 기준: 3분(180초) 이하 시 빨간 경고 표시 */
const TIMER_URGENT_SEC = 180;

/** 청소년 요금 비율: 성인 SEAT_COST 의 80% */
const TEEN_COST_RATIO = 0.8;

/**
 * 결제 수단 코드 (POST /api/payment 의 paymentMethod 필드값)
 * @enum {string}
 */
const PAY_METHOD = {
  CARD:   'CARD',
  CASH:   'CASH',
  SIMPLE: 'SIMPLE',
};


/* ══════════════════════════════════════════════════════════════════
   페이지 전역 상태
══════════════════════════════════════════════════════════════════ */

/**
 * 결제 화면 상태 객체
 * @type {{
 *   timerSec: number,
 *   timerInterval: number|null,
 *   selectedDiscountId: number,
 *   discountAmount: number,
 *   usePoint: number,
 *   memberPhone: string|null,
 *   memberPointBalance: number,
 *   selectedPayMethod: string|null,
 *   basePrice: number,
 * }}
 */
const state = {
  timerSec:            TIMER_TOTAL_SEC,
  timerInterval:       null,
  selectedDiscountId:  0,      // 0 = 할인 없음
  discountAmount:      0,
  usePoint:            0,
  memberPhone:         MEMBER_PHONE,       // 서버 세션에서 주입, 없으면 null
  memberPointBalance:  MEMBER_POINT,       // 서버 세션에서 주입, 없으면 0
  selectedPayMethod:   null,
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
   타이머 (UC-04 임시 좌석 점유 10분)
══════════════════════════════════════════════════════════════════ */

/**
 * 10분 카운트다운 타이머 시작.
 * 1초마다 DOM 업데이트 & 진행 바 축소.
 * 만료 시 onTimerExpired() 호출.
 */
function startTimer() {
  const countdownEl = document.getElementById('timer-countdown');
  const barEl       = document.getElementById('timer-bar');
  const timerEl     = document.getElementById('payment-timer');

  state.timerInterval = setInterval(() => {
    state.timerSec--;

    /* ── 남은 시간 텍스트 업데이트 (MM:SS) ── */
    const min = Math.floor(state.timerSec / 60);
    const sec = state.timerSec % 60;
    countdownEl.textContent =
      `${String(min).padStart(2, '0')}:${String(sec).padStart(2, '0')}`;

    /* ── 진행 바 너비 갱신 ── */
    const ratio = (state.timerSec / TIMER_TOTAL_SEC) * 100;
    barEl.style.width = `${ratio}%`;

    /* ── 긴급 상태 진입 (3분 이하) ── */
    if (state.timerSec <= TIMER_URGENT_SEC) {
      countdownEl.classList.add('is-urgent');
      timerEl.classList.add('is-urgent');
    }

    /* ── 만료 처리 ── */
    if (state.timerSec <= 0) {
      clearInterval(state.timerInterval);
      state.timerInterval = null;
      onTimerExpired();
    }
  }, 1000);
}

/**
 * 타이머 정지 (결제 성공 후 호출).
 */
function stopTimer() {
  if (state.timerInterval !== null) {
    clearInterval(state.timerInterval);
    state.timerInterval = null;
  }
}

/**
 * 타이머 만료 처리.
 * UC-04 명세: "시간 초과로 예매가 취소되었습니다." → /movie/list 이동.
 */
function onTimerExpired() {
  if (typeof CineOS !== 'undefined' && CineOS.modal) {
    CineOS.modal.alert({
      title:   '시간 초과',
      message: '시간 초과로 예매가 취소되었습니다.',
      onClose: () => {
        /* UC-03 예매 첫 화면(영화 목록)으로 재시작 */
        window.location.href = '/movie/list';
      },
    });
  } else {
    /* CineOS 모달이 없는 경우 fallback */
    alert('시간 초과로 예매가 취소되었습니다.');
    window.location.href = '/movie/list';
  }
}


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

/**
 * 할인 금액 계산.
 * @param {number} basePrice    할인 전 기본 금액
 * @param {number} discountId   선택된 할인 정책 ID (0 = 없음)
 * @returns {number}            할인 금액 (원)
 */
function calcDiscountAmount(basePrice, discountId) {
  if (discountId === 0) return 0;

  const policy = DISCOUNT_POLICIES.find(p => p.id === discountId);
  if (!policy) return 0;

  if (policy.discountType === 'RATIO') {
    /* 비율 할인: discountValue = 퍼센트 (예: 20 → 20%) */
    return Math.floor(basePrice * (policy.discountValue / 100));
  } else if (policy.discountType === 'WON') {
    /* 금액 할인: discountValue = 원 (예: 2000 → 2,000원) */
    return Math.min(policy.discountValue, basePrice);
  }

  return 0;
}

/**
 * 할인 옵션 카드의 금액 표시 텍스트 갱신.
 * 기본 요금이 확정된 후 한 번 호출.
 */
function updateDiscountAmountLabels() {
  document.querySelectorAll('.discount-option[data-discount-id]').forEach(optEl => {
    const id       = Number(optEl.dataset.discountId);
    const amountEl = optEl.querySelector('.discount-option__amount');
    if (!amountEl) return;

    if (id === 0) {
      amountEl.textContent = '0원';
      return;
    }

    const discount = calcDiscountAmount(state.basePrice, id);
    amountEl.textContent = discount > 0 ? `-${formatCurrency(discount)}` : '0원';
  });
}


/* ══════════════════════════════════════════════════════════════════
   가격 요약 UI 업데이트 (실시간)
══════════════════════════════════════════════════════════════════ */

/**
 * 가격 요약 섹션 전체 업데이트.
 * 할인 선택·포인트 입력 때마다 호출.
 * 최종 금액 = Math.max(0, basePrice - discountAmount - usePoint)
 */
function updatePriceUI() {
  const base     = state.basePrice;
  const discount = state.discountAmount;
  /* 포인트는 (기본 - 할인) 이하로 제한 */
  const maxAfterDiscount = Math.max(0, base - discount);
  const point    = Math.min(state.usePoint, maxAfterDiscount);
  const total    = Math.max(0, base - discount - point);

  /* ── 기본 요금 ── */
  document.getElementById('price-base').textContent = formatCurrency(base);

  /* ── 할인 금액 행 ── */
  const discountRow = document.getElementById('price-discount-row');
  if (discount > 0) {
    discountRow.hidden = false;
    document.getElementById('price-discount').textContent = `-${formatCurrency(discount)}`;
  } else {
    discountRow.hidden = true;
  }

  /* ── 포인트 사용 행 ── */
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

  /* ── 결제 수단 섹션 표시/숨김 ── */
  const payMethodSection = document.getElementById('pay-method-section');
  if (total === 0) {
    /* 전액 포인트 사용: 결제 수단 불필요 */
    payMethodSection.hidden = true;
  } else {
    payMethodSection.hidden = false;
    /* 이전에 "포인트 전액" 가상 수단이 설정됐으면 초기화 */
    if (state.selectedPayMethod === '_POINT_FULL') {
      state.selectedPayMethod = null;
    }
  }

  checkPayBtnEnabled();
}

/**
 * 포인트 적립 예정 안내 문구 업데이트.
 * @param {number} finalAmount 최종 결제 금액
 */
function updateBonusNote(finalAmount) {
  const noteEl = document.getElementById('price-bonus-note');
  if (!noteEl) return;

  /* 적립 정책 없거나 0원 결제 시 숨김 */
  if (!BONUS_POLICIES || BONUS_POLICIES.length === 0 || finalAmount === 0) {
    noteEl.hidden = true;
    return;
  }

  /* 첫 번째 정책 기준으로 적립 예상 포인트 계산 */
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
   할인 정책 선택 (UC-04)
══════════════════════════════════════════════════════════════════ */

/**
 * 할인 라디오 옵션 이벤트 리스너 등록.
 * 선택 변경 시 discountAmount 재계산 후 가격 UI 업데이트.
 */
function initDiscountOptions() {
  const container = document.getElementById('discount-options');
  if (!container) return;

  container.querySelectorAll('input[name="discount"]').forEach(radio => {
    radio.addEventListener('change', (e) => {
      const discountId = Number(e.target.value);
      state.selectedDiscountId = discountId;
      state.discountAmount     = calcDiscountAmount(state.basePrice, discountId);

      /* 포인트 사용량이 새 (기본-할인) 범위를 벗어나지 않도록 보정 */
      const maxPoint = Math.max(0, state.basePrice - state.discountAmount);
      if (state.usePoint > maxPoint) {
        state.usePoint = maxPoint;
        const pointInput = document.getElementById('point-input');
        if (pointInput) pointInput.value = maxPoint || '';
      }

      updatePriceUI();
    });
  });
}


/* ══════════════════════════════════════════════════════════════════
   포인트 인증 (UC-05)
══════════════════════════════════════════════════════════════════ */

/**
 * '포인트 인증하기' 버튼 클릭 → 휴대폰 인증 모달 오픈.
 * fragments/modal.html#phone-verify-content 의 HTML을 공용 모달에 삽입.
 */
function openPhoneVerifyModal() {
  const verifyContent = document.getElementById('phone-verify-content');
  if (!verifyContent || typeof CineOS === 'undefined' || !CineOS.modal) {
    console.error('[payment.js] phone-verify-content 또는 CineOS.modal을 찾을 수 없습니다.');
    return;
  }

  CineOS.modal.open({
    title:       '휴대폰 인증',
    content:     verifyContent.innerHTML,
    confirmText: '인증',
    cancelText:  '취소',
    onConfirm: () => {
      /* 모달 body 안에 삽입된 입력값 읽기 */
      const modalInput = document.querySelector('#modal-body #phone-verify-input');
      const phone      = modalInput ? modalInput.value.trim() : '';
      submitPhoneVerify(phone);
      /* return false로 모달 자동 닫기 방지 (submitPhoneVerify 내에서 수동 close) */
      return false;
    },
  });
}

/**
 * 휴대폰 인증 API 호출 (UC-05).
 * POST /api/auth/phone → 성공 시 GET /api/members/point
 * @param {string} phone 입력된 휴대폰 번호
 */
async function submitPhoneVerify(phone) {
  /* 형식 검증: 01X-XXXX-XXXX (하이픈 없이 10~11자리 숫자) */
  const phoneRegex = /^01[0-9]{8,9}$/;
  const modalErrorEl = document.querySelector('#modal-body #phone-verify-error');

  if (!phone || !phoneRegex.test(phone)) {
    /* UC-05 명세 에러 메시지 (임의 변경 금지) */
    if (modalErrorEl) {
      modalErrorEl.textContent = '올바른 휴대폰 번호 형식으로 입력해 주세요.';
    }
    return;
  }

  /* 기존 에러 초기화 */
  if (modalErrorEl) modalErrorEl.textContent = '';

  try {
    /* POST /api/auth/phone — 회원 조회 및 인증 */
    // TODO: 백엔드 AuthController 구현 후 응답 형식 확인
    await CineOS.api.post('/api/auth/phone', { phone });

    /* GET /api/members/point?phone= — 포인트 잔액 조회 (UC-06) */
    // TODO: 백엔드 MemberController 구현 후 응답 형식 확인
    const pointData     = await CineOS.api.get(`/api/members/point?phone=${encodeURIComponent(phone)}`);
    const pointBalance  = (pointData && typeof pointData.point === 'number')
      ? pointData.point
      : 0;

    /* 상태 갱신 */
    state.memberPhone        = phone;
    state.memberPointBalance = pointBalance;

    /* 모달 닫기 */
    CineOS.modal.close();

    /* UI 전환: 인증 버튼 숨기고 포인트 사용 UI 표시 */
    document.getElementById('point-auth-wrap').hidden = true;
    const pointUseWrap = document.getElementById('point-use-wrap');
    pointUseWrap.hidden = false;

    /* 보유 포인트 표시 */
    document.getElementById('point-balance-display').textContent =
      formatPoint(pointBalance);

    /* 성공 토스트 알림 */
    if (typeof CineOS !== 'undefined' && CineOS.alert) {
      CineOS.alert.show({ type: 'success', message: '인증이 완료되었습니다.' });
    }

  } catch (err) {
    /* UC-05 명세 에러 메시지 (임의 변경 금지) */
    const status  = err?.status ?? err?.response?.status;
    const message = status === 404
      ? '등록되지 않은 번호입니다.'
      : '인증 중 오류가 발생했습니다. 다시 시도해 주세요.';

    if (modalErrorEl) {
      modalErrorEl.textContent = message;
    }
  }
}


/* ══════════════════════════════════════════════════════════════════
   포인트 사용 (UC-06)
══════════════════════════════════════════════════════════════════ */

/**
 * 포인트 입력 / 전액 사용 이벤트 초기화.
 * - 직접 입력: 0 ~ min(보유포인트, 기본요금-할인) 범위 유지
 * - 전액 사용: 위 범위의 최댓값으로 자동 입력
 */
function initPointInput() {
  const pointInput  = document.getElementById('point-input');
  const btnPointAll = document.getElementById('btn-point-all');
  const pointError  = document.getElementById('point-error');

  if (!pointInput) return;

  /* 직접 입력 이벤트 */
  pointInput.addEventListener('input', () => {
    const val       = parseInt(pointInput.value, 10) || 0;
    const maxUsable = Math.max(0, state.basePrice - state.discountAmount);
    const maxPoint  = Math.min(state.memberPointBalance, maxUsable);

    if (val > maxPoint) {
      /* 한도 초과: 최대값으로 보정 */
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
      const maxUsable = Math.max(0, state.basePrice - state.discountAmount);
      const useAll    = Math.min(state.memberPointBalance, maxUsable);

      pointInput.value = useAll;
      state.usePoint   = useAll;

      if (pointError) pointError.hidden = true;
      updatePriceUI();
    });
  }
}


/* ══════════════════════════════════════════════════════════════════
   결제 수단 선택
══════════════════════════════════════════════════════════════════ */

/**
 * 결제 수단 버튼 클릭 이벤트 초기화.
 * 선택 시 .is-selected 클래스 토글 & aria-pressed 업데이트.
 */
function initPayMethodButtons() {
  const buttons = document.querySelectorAll('.pay-method-btn');

  buttons.forEach(btn => {
    btn.addEventListener('click', () => {
      /* 기존 선택 해제 */
      buttons.forEach(b => {
        b.classList.remove('is-selected');
        b.setAttribute('aria-pressed', 'false');
      });

      /* 클릭한 버튼 선택 */
      btn.classList.add('is-selected');
      btn.setAttribute('aria-pressed', 'true');
      state.selectedPayMethod = btn.dataset.method;

      checkPayBtnEnabled();
    });
  });
}


/* ══════════════════════════════════════════════════════════════════
   결제 버튼 활성화 조건 체크
══════════════════════════════════════════════════════════════════ */

/**
 * '결제하기' 버튼 활성화 여부 결정.
 * 활성화 조건:
 *   - 총 결제 금액 === 0원 (전액 포인트 사용)
 *   - OR 결제 수단이 선택됨
 */
function checkPayBtnEnabled() {
  const btnPay = document.getElementById('btn-pay');
  if (!btnPay) return;

  const total = Math.max(
    0,
    state.basePrice - state.discountAmount - state.usePoint
  );

  const isEnabled = total === 0 || state.selectedPayMethod !== null;

  btnPay.disabled = !isEnabled;
  btnPay.setAttribute('aria-disabled', String(!isEnabled));
}


/* ══════════════════════════════════════════════════════════════════
   결제 요청 (UC-04)
══════════════════════════════════════════════════════════════════ */

/**
 * '결제하기' 버튼 클릭 → POST /api/payment.
 *
 * 성공: stopTimer() → /payment/result?reservationId= 이동
 * 실패: "결제에 실패하였습니다. 다시 시도해 주세요." → 버튼 복원
 */
async function submitPayment() {
  const btnPay = document.getElementById('btn-pay');
  if (!btnPay || btnPay.disabled) return;

  const base     = state.basePrice;
  const discount = state.discountAmount;
  const maxAfterDiscount = Math.max(0, base - discount);
  const point    = Math.min(state.usePoint, maxAfterDiscount);
  const total    = Math.max(0, base - discount - point);

  /**
   * 결제 요청 DTO (API 명세 기준)
   * @see KiOsk 명세서 - API 고객 영역.csv
   * @type {{
   *   reservationId: number,
   *   discountPolicyId: number|null,
   *   usePoint: number,
   *   paymentMethod: string|null,
   * }}
   */
  const payload = {
    reservationId:    RESERVATION_ID,
    discountPolicyId: state.selectedDiscountId > 0 ? state.selectedDiscountId : null,
    usePoint:         point,
    /* 0원 결제 시 paymentMethod 불필요 → null */
    paymentMethod:    total === 0 ? null : state.selectedPayMethod,
  };

  /* ── 버튼 로딩 상태 ── */
  const originalText = btnPay.textContent;
  btnPay.disabled = true;
  btnPay.setAttribute('aria-disabled', 'true');
  btnPay.textContent = '결제 처리 중...';

  if (typeof CineOS !== 'undefined' && CineOS.loading) {
    CineOS.loading.show();
  }

  try {
    /* POST /api/payment */
    // TODO: 백엔드 PaymentController 구현 후 엔드포인트·응답 형식 확인
    await CineOS.api.post('/api/payment', payload);

    /* 결제 성공 → 타이머 정지 */
    stopTimer();

    if (typeof CineOS !== 'undefined' && CineOS.loading) {
      CineOS.loading.hide();
    }

    /* UC-07 결제 완료 화면으로 이동 */
    window.location.href =
      `/payment/result?reservationId=${encodeURIComponent(RESERVATION_ID)}`;

  } catch (err) {
    if (typeof CineOS !== 'undefined' && CineOS.loading) {
      CineOS.loading.hide();
    }

    /* UC-04 명세 에러 메시지 (임의 변경 금지) */
    if (typeof CineOS !== 'undefined' && CineOS.modal) {
      CineOS.modal.alert({
        title:   '결제 실패',
        message: '결제에 실패하였습니다. 다시 시도해 주세요.',
      });
    } else {
      alert('결제에 실패하였습니다. 다시 시도해 주세요.');
    }

    /* 버튼 복원 */
    btnPay.disabled = false;
    btnPay.setAttribute('aria-disabled', 'false');
    btnPay.textContent = originalText;
  }
}


/* ══════════════════════════════════════════════════════════════════
   예매 날짜·시간 초기화 (SSR 날짜 → JS 포맷)
══════════════════════════════════════════════════════════════════ */

/**
 * booking-datetime 요소의 data-start / data-end 속성값을
 * 사람이 읽기 좋은 형식으로 변환하여 DOM에 반영.
 */
function initBookingDatetime() {
  const el = document.getElementById('booking-datetime');
  if (!el) return;

  const start = el.dataset.start || '';
  const end   = el.dataset.end   || '';
  el.textContent = formatDatetime(start, end);
}

/**
 * 좌석 번호 목록 초기화.
 * SSR로 표시되지 않은 경우 JS 상수 SEAT_NUMBERS를 이용해 DOM 업데이트.
 */
function initSeatsDisplay() {
  const seatsEl = document.getElementById('booking-seats');
  if (!seatsEl) return;

  /* 이미 SSR로 내용이 채워진 경우 (하이픈만 있으면 교체) */
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
 *   1. 기본 요금 계산
 *   2. 인증 상태 초기화 (서버 세션에 member 있으면 즉시 포인트 UI 표시)
 *   3. 예매 정보 날짜 포맷 변환
 *   4. 좌석 번호 표시
 *   5. 할인 옵션 금액 레이블 갱신
 *   6. 이벤트 리스너 등록
 *   7. 초기 가격 UI 렌더링
 *   8. 타이머 시작
 */
function initPage() {

  /* 1. 기본 요금 계산 */
  state.basePrice = calcBasePrice();

  /* 2. 서버 세션에 member가 이미 있는 경우: 인증 UI 즉시 전환 */
  if (state.memberPhone !== null) {
    document.getElementById('point-auth-wrap').hidden = true;
    const pointUseWrap = document.getElementById('point-use-wrap');
    if (pointUseWrap) {
      pointUseWrap.hidden = false;
      const balanceEl = document.getElementById('point-balance-display');
      if (balanceEl) {
        balanceEl.textContent = formatPoint(state.memberPointBalance);
      }
    }
  }

  /* 3. 예매 날짜·시간 포맷 변환 */
  initBookingDatetime();

  /* 4. 좌석 번호 표시 */
  initSeatsDisplay();

  /* 5. 할인 옵션 금액 레이블 갱신 */
  updateDiscountAmountLabels();

  /* 6. 이벤트 리스너 등록 */
  initDiscountOptions();
  initPayMethodButtons();
  initPointInput();

  /* 포인트 인증 버튼 */
  const btnPointAuth = document.getElementById('btn-point-auth');
  if (btnPointAuth) {
    btnPointAuth.addEventListener('click', openPhoneVerifyModal);
  }

  /* 결제하기 버튼 */
  const btnPay = document.getElementById('btn-pay');
  if (btnPay) {
    btnPay.addEventListener('click', submitPayment);
  }

  /* 7. 초기 가격 UI 렌더링 */
  updatePriceUI();

  /* 8. 타이머 시작 */
  startTimer();
}

/* ══════════════════════════════════════════════════════════════════
   DOM 준비 후 실행
══════════════════════════════════════════════════════════════════ */

document.addEventListener('DOMContentLoaded', initPage);

/**
 * 페이지 이탈(뒤로가기·탭 닫기 등) 시 타이머 인터벌 정리.
 * 실제 좌석 임시 점유 해제는 서버(BackEnd) TTL 처리에 의존.
 */
window.addEventListener('beforeunload', () => {
  stopTimer();
});
