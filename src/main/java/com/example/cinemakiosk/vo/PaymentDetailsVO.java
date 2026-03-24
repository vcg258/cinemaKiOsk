package com.example.cinemakiosk.vo;

import com.example.cinemakiosk.dto.PaymentDetailsDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDetailsVO {
    private String id;                       // 인덱스
    private ReservationDetailsVO reservation;// 예매 정보
    private BonusPolicyVO bonusPolicy;       // 사용한 적립 정책
    private CouponVO couponNum;              // 사용한 할인 쿠폰, 없는 경우 null
    private Long cost;                       // 결제 금액
    private LocalDateTime time;              // 결제 시간
    private Long usePoint;                   // 사용 포인트 기본값 0
    private String status;                   // ENUM ('PAY','RETURN','FAIL'), 결제 완료, 환불, 실패

    public static PaymentDetailsDTO toDTO(PaymentDetailsVO paymentDetailsVO){
        return PaymentDetailsDTO.builder()
                .id(paymentDetailsVO.getId())
                .reservation(ReservationDetailsVO.toDTO(paymentDetailsVO.getReservation()))
                .bonusPolicy(BonusPolicyVO.toDTO(paymentDetailsVO.getBonusPolicy()))
                .couponNum(CouponVO.toDTO(paymentDetailsVO.getCouponNum()))
                .cost(paymentDetailsVO.getCost())
                .time(paymentDetailsVO.getTime())
                .usePoint(paymentDetailsVO.getUsePoint())
                .status(paymentDetailsVO.getStatus())
                .build();
    }
}
