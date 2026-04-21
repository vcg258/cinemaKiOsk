package com.example.cinemakiosk.vo;

import com.example.cinemakiosk.dto.TheaterDTO;
import lombok.*;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TheaterVO {
    private Long no; // 상영관 번호
    private SeatPolicyVO seatPolicy; // 좌석정책 FK
    private Long policyId; // 좌석정책 FK
    private Long cleanupTime; // 정리시간(분)

    /**
     * VO -> DTO
     * @param theaterVO
     * @return DTO
     */
    public static TheaterDTO toDTO(TheaterVO theaterVO){

        return TheaterDTO.builder()
                .no(theaterVO.getNo())
                .policyId(theaterVO.getPolicyId())
                .seatPolicy(SeatPolicyVO.toDTO(theaterVO.getSeatPolicy()))
                .cleanupTime(theaterVO.getCleanupTime())
                .build();
    }
}
