package com.example.cinemakiosk.vo;

import com.example.cinemakiosk.dto.SeatPolicyDTO;
import lombok.*;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SeatPolicyVO {
    private Long policyId; // 좌석 아이디
    private String name; // 좌석 이름
    private Long cost; // 좌석 비용

    /**
     * VO -> DTO
     * @param seatPolicyVO
     * @return DTO
     */
    public static SeatPolicyDTO toDTO(SeatPolicyVO seatPolicyVO){

        return SeatPolicyDTO.builder()
                .policyId(seatPolicyVO.getPolicyId())
                .name(seatPolicyVO.getName())
                .cost(seatPolicyVO.getCost())
                .build();
    }
}
