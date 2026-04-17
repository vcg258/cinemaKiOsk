package com.example.cinemakiosk.dto;

import com.example.cinemakiosk.domain.PaymentDetailsEntity;
import com.example.cinemakiosk.domain.enums.Status;
import com.example.cinemakiosk.domain.PointHistoryEntity;
import com.example.cinemakiosk.vo.PaymentDetailsVO;
import com.example.cinemakiosk.vo.PointHistoryVO;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDetailsDTO {
    private String id;                       // 인덱스
    private ReservationDetailsDTO reservation;// 예매 정보
    private BonusPolicyDTO bonusPolicy;       // 사용한 적립 정책
    private CouponDTO couponNum;              // 사용한 할인 쿠폰, 없는 경우 null
    private Long cost;                       // 결제 금액
    private LocalDateTime createAt;              // 결제 시간
    private Long usePoint;                   // 사용 포인트 기본값 0
    private Status status;                   // ENUM ('PAY','RETURN','FAIL'), 결제 완료, 환불, 실패

    /**
     * DTO -> Entity
     * @param paymentDetailsDTO
     * @return Entity
     */
    public static PaymentDetailsEntity toEntity(PaymentDetailsDTO paymentDetailsDTO){
        return PaymentDetailsEntity.builder()
                .id(paymentDetailsDTO.getId())
                .reservationDetailsEntity(ReservationDetailsDTO.toEntity(paymentDetailsDTO.getReservation()))
                .bonusPolicyEntity(BonusPolicyDTO.toEntity(paymentDetailsDTO.getBonusPolicy()))
                .couponEntity(CouponDTO.toEntity(paymentDetailsDTO.getCouponNum()))
                .cost(paymentDetailsDTO.getCost())
                .createAt(paymentDetailsDTO.getCreateAt())
                .usePoint(paymentDetailsDTO.getUsePoint())
                .status(paymentDetailsDTO.getStatus())
                .build();
    }

    /**
     * DTO -> VO
     * @param paymentDetailsDTO
     * @return VO
     */
    public static PaymentDetailsVO toVO(PaymentDetailsDTO paymentDetailsDTO){
        return PaymentDetailsVO.builder()
                .id(paymentDetailsDTO.getId())
                .reservation(ReservationDetailsDTO.toVO(paymentDetailsDTO.getReservation()))
                .bonusPolicy(BonusPolicyDTO.toVO(paymentDetailsDTO.getBonusPolicy()))
                .couponNum(CouponDTO.toVO(paymentDetailsDTO.getCouponNum()))
                .cost(paymentDetailsDTO.getCost())
                .createAt(paymentDetailsDTO.getCreateAt())
                .usePoint(paymentDetailsDTO.getUsePoint())
                .status(paymentDetailsDTO.getStatus())
                .build();
    }

}
