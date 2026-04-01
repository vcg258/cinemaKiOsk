package com.example.cinemakiosk.dto;

import com.example.cinemakiosk.domain.SeatPolicyEntity;
import com.example.cinemakiosk.domain.TheaterEntity;
import com.example.cinemakiosk.vo.SeatPolicyVO;
import com.example.cinemakiosk.vo.TheaterVO;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SeatPolicyDTO {
    private Long policyId; // 좌석 아이디
    private String name; // 좌석 이름
    private Long cost; // 좌석 비용

    /**
     * DTO -> Entity
     * @param seatPolicyDTO
     * @return Entity
     */
    public static SeatPolicyEntity toEntity(SeatPolicyDTO seatPolicyDTO){
        return SeatPolicyEntity.builder()
                .policyId(seatPolicyDTO.getPolicyId())
                .name(seatPolicyDTO.getName())
                .cost(seatPolicyDTO.getCost())
                .build();
    }

    /**
     * DTO -> VO
     * @param seatPolicyDTO
     * @return VO
     */
    public static SeatPolicyVO toVO(SeatPolicyDTO seatPolicyDTO){
        return SeatPolicyVO.builder()
                .policyId(seatPolicyDTO.getPolicyId())
                .name(seatPolicyDTO.getName())
                .cost(seatPolicyDTO.getCost())
                .build();
    }
}
