package com.example.cinemakiosk.dto;

import com.example.cinemakiosk.vo.CuponeDTO;
import com.example.cinemakiosk.vo.PaymentDetailsVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDetailsDTO {
    private String id;                       // 인덱스
    private ReservationDetailsDTO reservation;// 예매 정보
    private BonusPolicyDTO bonusPolicy;       // 사용한 적립 정책
    private CuponeDTO couponNum;              // 사용한 할인 쿠폰, 없는 경우 null
    private Long cost;                       // 결제 금액
    private LocalDateTime time;              // 결제 시간
    private Long usePoint;                   // 사용 포인트 기본값 0
    private String status;                   // ENUM ('PAY','RETURN','FAIL'), 결제 완료, 환불, 실패


    public static PaymentDetailsVO toVO(PaymentDetailsDTO paymentDetailsDTO){
        return PaymentDetailsVO.builder()
                .id(paymentDetailsDTO.getId())
                .reservation(ReservationDetailsDTO.toVO(paymentDetailsDTO.getReservation()))
                .bonusPolicy(BonusPolicyDTO.toVO(paymentDetailsDTO.getBonusPolicy()))
                .couponNum(CuponeDTO.toVO(paymentDetailsDTO.getCouponNum()))
                .cost(paymentDetailsDTO.getCost())
                .time(paymentDetailsDTO.getTime())
                .usePoint(paymentDetailsDTO.getUsePoint())
                .status(paymentDetailsDTO.getStatus())
                .build();
    }

}
