package com.example.cinemakiosk.service;

import com.example.cinemakiosk.domain.enums.Status;
import com.example.cinemakiosk.dto.CouponDTO;
import com.example.cinemakiosk.dto.PaymentDetailsDTO;
import com.example.cinemakiosk.dto.PointHistoryDTO;
import com.example.cinemakiosk.mapper.PointHistoryMapper;
import com.example.cinemakiosk.vo.PointHistoryVO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

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
    @Transactional
    public void refund(String reservationId){
        // 지정 결제 내역 조회
        PaymentDetailsDTO paymentDetailsOne = paymentDetailsService.read(reservationId);
        paymentDetailsService.updateToReturn(paymentDetailsOne);

        // 포인트 복구
        List<PointHistoryVO> pointHistoryVO = pointHistoryMapper.selectByPayment(paymentDetailsOne.getId());
        for (PointHistoryVO pointHistoryVO1 : pointHistoryVO) {
            memberService.pointHistoryCancel(PointHistoryVO.toDTO(pointHistoryVO1)); // 들어갈 값 PointHistoryDTO
            log.info("포인트 복구 : {}", pointHistoryVO1);
        }

        String couponNum = paymentDetailsOne.getCouponNum().getCouponNum();
        if (discountPolicyService.getCoupon(couponNum).isStatus()) {
            throw new IllegalStateException("이미 환불 처리 된 쿠폰...");
        }

        // 쿠폰 복구
        if (paymentDetailsOne.getCouponNum() != null) {
            CouponDTO couponDTO = CouponDTO.builder()
                    .couponNum(paymentDetailsOne.getCouponNum().getCouponNum())
                    .status(true)
                    .build();
            discountPolicyService.updateStatus(couponDTO);
        } else {
            log.info("쿠폰 미사용");
        }
    }
}
