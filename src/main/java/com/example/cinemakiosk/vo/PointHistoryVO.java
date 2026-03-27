package com.example.cinemakiosk.vo;

import com.example.cinemakiosk.domain.PointHistoryEntity.PointHistoryEntity;
import com.example.cinemakiosk.domain.PointHistoryEntity.Type;
import com.example.cinemakiosk.dto.MemberDTO;
import com.example.cinemakiosk.dto.PaymentDetailsDTO;
import com.example.cinemakiosk.dto.PointHistoryDTO;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PointHistoryVO {
    private Long pointId; // 포인트 인덱스
    private PaymentDetailsVO paymentId; // 결제 고유번호 FK
    private MemberVO phone; // 회원번호 FK
    private Type type; // 적립 / 사용 ('EARN', 'USE')
    private Integer amountPoint; // 사용할 포인트
    private LocalDateTime createAt; // 포인트 변경일

    /**
     * VO -> DTO
     * @param pointHistoryVO
     * @return DTO
     */
    public static PointHistoryDTO toDTO(PointHistoryVO pointHistoryVO){
        return PointHistoryDTO.builder()
                .pointId(pointHistoryVO.getPointId())
                .paymentId(PaymentDetailsVO.toDTO(pointHistoryVO.getPaymentId()))
                .phone(MemberVO.toDTO(pointHistoryVO.getPhone()))
                .type(pointHistoryVO.getType())
                .amountPoint(pointHistoryVO.getAmountPoint())
                .createAt(pointHistoryVO.getCreateAt())
                .build();
    }
}
