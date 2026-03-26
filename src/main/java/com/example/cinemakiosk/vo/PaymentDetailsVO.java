package com.example.cinemakiosk.vo;

import com.example.cinemakiosk.domain.PointHistoryEntity.PointHistoryEntity;
import com.example.cinemakiosk.dto.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private List<PointHistoryVO> pointHistories;


    /**
     * VO -> DTO
     * @param paymentDetailsVO
     * @return DTO
     */
    public static PaymentDetailsDTO toDTO(PaymentDetailsVO paymentDetailsVO){
        //OneToMany 변수는 본인 객체를 제외한 값만 받기. 순환참조 방지.
        List<PointHistoryVO> pointHistoryVOs = paymentDetailsVO.getPointHistories();
        List<PointHistoryDTO> pointHistoryDTOs = new ArrayList<>();


        for (PointHistoryVO pointHistoryVO : pointHistoryVOs){
            //pk 만 받아오기.
            PointHistoryDTO pointHistoryDTO = PointHistoryDTO.builder()
                    .pointId(pointHistoryVO.getPointId())
                    .build();

            pointHistoryDTOs.add(pointHistoryDTO);
        }

        return PaymentDetailsDTO.builder()
                .id(paymentDetailsVO.getId())
                .reservation(ReservationDetailsVO.toDTO(paymentDetailsVO.getReservation()))
                .bonusPolicy(BonusPolicyVO.toDTO(paymentDetailsVO.getBonusPolicy()))
                .couponNum(CouponVO.toDTO(paymentDetailsVO.getCouponNum()))
                .cost(paymentDetailsVO.getCost())
                .time(paymentDetailsVO.getTime())
                .usePoint(paymentDetailsVO.getUsePoint())
                .status(paymentDetailsVO.getStatus())
                .pointHistories(pointHistoryDTOs)
                .build();
    }
}
