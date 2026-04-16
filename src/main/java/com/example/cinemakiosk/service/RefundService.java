package com.example.cinemakiosk.service;

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
        log.info("0");

        // 지정 결제 내역 조회
        PaymentDetailsDTO paymentDetailsOne = paymentDetailsService.read(reservationId);

        // TODO 1. 예매/결제 내역 상태 변경 (PAY -> RETURN)


        // 2. 포인트 복구
        List<PointHistoryVO> pointHistoryVO = pointHistoryMapper.selectByPayment(paymentDetailsOne.getId());
        //todo2 : List에서 earn인지 use인지 확인 후 반대 내역 만들고 member에 반영
//        memberService.pointHistoryCancel(PointHistoryVO.toDTO(pointHistoryVO)); // 들어갈 값 PointHistoryDTO
        log.info("3");
        // 3. 쿠폰 복구
        if (paymentDetailsOne.getCouponNum() != null) {
            log.info("4");
            CouponDTO couponDTO = CouponDTO.builder()
                    .couponNum(paymentDetailsOne.getCouponNum().getCouponNum())
                    .status(true)
                    .build();
            log.info("5");
            discountPolicyService.updateStatus(couponDTO);
            log.info("6");
        } else {
            log.info("7");
            log.info("쿠폰 미사용");
        }


    }
}
