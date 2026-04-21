package com.example.cinemakiosk.vo;

import com.example.cinemakiosk.domain.enums.Type;
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
    private String paymentId; // 결제 고유번호 FK
    private String phone; // 회원번호 FK
    private Type type; // 적립 / 사용 ('EARN', 'USE')
    private Integer amountPoint; // 사용할 포인트
    private LocalDateTime createAt; // 포인트 변경일

    private String title;
    private MemberVO member; // resultMap

    /**
     * VO -> DTO
     * @param pointHistoryVO
     * @return DTO
     */
    public static PointHistoryDTO toDTO(PointHistoryVO pointHistoryVO){
        return PointHistoryDTO.builder()
                .pointId(pointHistoryVO.getPointId())
                .paymentId(pointHistoryVO.getPaymentId())
                .phone(pointHistoryVO.getPhone())
                .type(pointHistoryVO.getType())
                .amountPoint(pointHistoryVO.getAmountPoint())
                .createAt(pointHistoryVO.getCreateAt())
                .build();
    }
}
