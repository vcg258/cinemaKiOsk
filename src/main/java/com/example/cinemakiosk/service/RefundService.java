package com.example.cinemakiosk.service;

import com.example.cinemakiosk.dto.CouponDTO;
import com.example.cinemakiosk.dto.PaymentDetailsDTO;
import com.example.cinemakiosk.dto.PointHistoryDTO;
import com.example.cinemakiosk.mapper.PointHistoryMapper;
import com.example.cinemakiosk.vo.PointHistoryVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class RefundService {
    private final DiscountPolicyService discountPolicyService;
    private final MemberService memberService;
    private final PaymentDetailsService paymentDetailsService;
    private final PointHistoryMapper pointHistoryMapper;

    /**
     * 환불
     * @param reservationId 환불할 내역
     */
    public void refund(String reservationId){
        // 지정 결제 내역 조회
        PaymentDetailsDTO paymentDetailsOne = paymentDetailsService.read(reservationId);

        // TODO 1. 예매/결제 내역 상태 변경 (PAY -> RETURN)


        // 2. 포인트 복구
        PointHistoryVO pointHistoryVO = pointHistoryMapper.selectByPayment(paymentDetailsOne.getId());
        memberService.pointHistoryCancel(PointHistoryVO.toDTO(pointHistoryVO)); // 들어갈 값 PointHistoryDTO

        // 3. 쿠폰 복구
        if (paymentDetailsOne.getCouponNum() != null) {
            CouponDTO couponDTO = CouponDTO.builder()
                    .couponNum(paymentDetailsOne.getCouponNum().getCouponNum())
                    .status(true)
                    .build();
            discountPolicyService.updateStatus(couponDTO);
        } else {
            log.info("쿠폰 미사용");
        }

        // TODO 4. 통계 데이터 업데이트


    }
}
