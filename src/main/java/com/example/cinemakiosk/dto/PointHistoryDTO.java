package com.example.cinemakiosk.dto;

import com.example.cinemakiosk.domain.MemberEntity;
import com.example.cinemakiosk.domain.PaymentDetailsEntity;
import com.example.cinemakiosk.domain.PointHistoryEntity;
import com.example.cinemakiosk.domain.enums.Type;
import com.example.cinemakiosk.vo.PointHistoryVO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PointHistoryDTO {
    private Long pointId; // 포인트 인덱스
    private PaymentDetailsDTO paymentDetails; // 결제 고유번호 FK (JPA 용도)
    private String paymentId;
    private MemberDTO member; // 회원번호 FK (JPA 용도)
    private String phone; // 회원 번호
    private Type type; // 적립 / 사용 ('EARN', 'USE')
    private Integer amountPoint; // 사용할 포인트
    private LocalDateTime createAt; // 포인트 변경일

    /**
     * DTO -> Entity
     * @param pointHistoryDTO
     * @return Entity
     */
    public static PointHistoryEntity toEntity(PointHistoryDTO pointHistoryDTO){


        PaymentDetailsEntity paymentDetailsEntity = null;
        if (pointHistoryDTO.getPaymentDetails() != null) { // 만약 객체가 들어온다면 변환
            paymentDetailsEntity = PaymentDetailsEntity.builder()
                    .id(pointHistoryDTO.getPaymentDetails().getId())
                    .build();
        } else if (pointHistoryDTO.getPaymentId() != null) { // PaymentId (String) 으로 들어온다면 변환
            paymentDetailsEntity = PaymentDetailsEntity.builder()
                    .id(pointHistoryDTO.getPaymentId())
                    .build();
        }

        MemberEntity memberEntity = null;
        if (pointHistoryDTO.getMember() != null) {
            memberEntity = MemberEntity.builder()
                    .phone(pointHistoryDTO.getMember().getPhone())
                    .build();
        } else if (pointHistoryDTO.getPhone() != null) {
            memberEntity = MemberEntity.builder()
                    .phone(pointHistoryDTO.getPhone())
                    .build();
        }

        if (paymentDetailsEntity == null || memberEntity == null) {
            throw new IllegalArgumentException("NotNull(PaymentId, MemberPhone) FK 없음 넣어줘야함");
        }

        return PointHistoryEntity.builder()
                .pointId(pointHistoryDTO.getPointId())
                .paymentDetailsEntity(paymentDetailsEntity)
                .memberEntity(memberEntity)
                .type(pointHistoryDTO.getType())
                .amountPoint(pointHistoryDTO.getAmountPoint())
                .createAt(pointHistoryDTO.getCreateAt())
                .build();
    }

    /**
     * DTO -> VO
     * @param pointHistoryDTO
     * @return VO
     */
    public static PointHistoryVO toVO(PointHistoryDTO pointHistoryDTO){
        return PointHistoryVO.builder()
                .pointId(pointHistoryDTO.getPointId())
                .paymentId(pointHistoryDTO.getPaymentId())
                .phone(pointHistoryDTO.getPhone())
                .type(pointHistoryDTO.getType())
                .amountPoint(pointHistoryDTO.getAmountPoint())
                .createAt(pointHistoryDTO.getCreateAt())
                .build();
    }
}
