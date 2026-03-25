package com.example.cinemakiosk.dto;

import com.example.cinemakiosk.domain.MemberEntity;
import com.example.cinemakiosk.domain.PaymentDetailsEntity.PaymentDetailsEntity;
import com.example.cinemakiosk.domain.PointHistoryEntity.PointHistoryEntity;
import com.example.cinemakiosk.domain.PointHistoryEntity.Type;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointHistoryDTO {
    private Long pointId; // 포인트 인덱스
    private String paymentId; // 결제 고유번호 FK
    private String phone; // 회원번호 FK
    private Type type; // 적립 / 사용 ('EARN', 'USE')
    private Integer amountPoint; // 사용할 포인트

    public static PointHistoryEntity toEntity(PointHistoryDTO pointHistoryDTO, PaymentDetailsEntity paymentDetailsEntity, MemberEntity memberEntity) {
        return PointHistoryEntity.builder()
                .pointId(pointHistoryDTO.getPointId())
                .paymentDetailsEntity(paymentDetailsEntity)
                .memberEntity(memberEntity)
                .type(pointHistoryDTO.getType())
                .amountPoint(pointHistoryDTO.getAmountPoint())
                .build();
    }
}
