package com.example.cinemakiosk.dto;

import com.example.cinemakiosk.domain.ScheduleEntity;
import com.example.cinemakiosk.domain.SeatPolicyEntity;
import com.example.cinemakiosk.domain.TheaterEntity;
import com.example.cinemakiosk.vo.ScheduleVO;
import com.example.cinemakiosk.vo.SeatPolicyVO;
import com.example.cinemakiosk.vo.TheaterVO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TheaterDTO {
    private Long no; // 상영관 번호
    private SeatPolicyDTO seatPolicy; // 좌석정책 FK
    private Long policyId; // 좌석정책 FK
    private Long cleanupTime; // 정리시간(분)

    /**
     * DTO -> Entity
     * @param theaterDTO
     * @return Entity
     */
    public static TheaterEntity toEntity(TheaterDTO theaterDTO){

        SeatPolicyEntity seatPolicyEntity = null;
        if (theaterDTO.getSeatPolicy() != null) {
            seatPolicyEntity = SeatPolicyEntity.builder()
                    .policyId(theaterDTO.getSeatPolicy().getPolicyId())
                    .build();
        } else if (theaterDTO.getPolicyId() != null) {
            seatPolicyEntity = SeatPolicyEntity.builder()
                    .policyId(theaterDTO.getPolicyId())
                    .build();
        }

        if (seatPolicyEntity == null) {
            throw new IllegalArgumentException("필수값 누락 NotNull(Theater policyId)");
        }

        return TheaterEntity.builder()
                .no(theaterDTO.getNo())
                .seatPolicyEntity(seatPolicyEntity)
                .cleanupTime(theaterDTO.getCleanupTime())
                .build();
    }

    /**
     * DTO -> VO
     * @param theaterDTO
     * @return VO
     */
    public static TheaterVO toVO(TheaterDTO theaterDTO){

        return TheaterVO.builder()
                .no(theaterDTO.getNo())
                .policyId(theaterDTO.getPolicyId())
                .cleanupTime(theaterDTO.getCleanupTime())
                .build();
    }
}
