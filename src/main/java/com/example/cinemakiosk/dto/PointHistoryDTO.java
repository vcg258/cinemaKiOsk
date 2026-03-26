package com.example.cinemakiosk.dto;

import com.example.cinemakiosk.domain.PointHistoryEntity.PointHistoryEntity;
import com.example.cinemakiosk.domain.PointHistoryEntity.Type;
import com.example.cinemakiosk.vo.PointHistoryVO;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PointHistoryDTO {
    private Long pointId; // 포인트 인덱스
    private PaymentDetailsDTO paymentId; // 결제 고유번호 FK
    private MemberDTO phone; // 회원번호 FK
    private Type type; // 적립 / 사용 ('EARN', 'USE')
    private Long amountPoint; // 사용할 포인트
    private LocalDateTime createAt; // 포인트 변경일

    /**
     * DTO -> VO
     * @param pointHistoryDTO
     * @return VO
     */
    public static PointHistoryVO toVO(PointHistoryDTO pointHistoryDTO){
        return PointHistoryVO.builder()
                .pointId(pointHistoryDTO.getPointId())
                .paymentId(PaymentDetailsDTO.toVO(pointHistoryDTO.getPaymentId()))
                .phone(MemberDTO.toVO(pointHistoryDTO.getPhone()))
                .type(pointHistoryDTO.getType())
                .amountPoint(pointHistoryDTO.getAmountPoint())
                .createAt(pointHistoryDTO.getCreateAt())
                .build();
    }

    /**
     * DTO -> Entity
     * @param pointHistoryDTO
     * @return Entity
     */
    public static PointHistoryEntity toEntity(PointHistoryDTO pointHistoryDTO){
        return PointHistoryEntity.builder()
                .pointId(pointHistoryDTO.getPointId())
                .paymentDetailsEntity(PaymentDetailsDTO.toEntity(pointHistoryDTO.getPaymentId()))
                .memberEntity(MemberDTO.toEntity(pointHistoryDTO.getPhone()))
                .type(pointHistoryDTO.getType())
                .amountPoint(pointHistoryDTO.getAmountPoint())
                .createAt(pointHistoryDTO.getCreateAt())
                .build();
    }
}
