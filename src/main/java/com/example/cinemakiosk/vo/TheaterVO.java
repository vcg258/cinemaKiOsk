package com.example.cinemakiosk.vo;

import com.example.cinemakiosk.domain.ScheduleEntity;
import com.example.cinemakiosk.domain.TheaterEntity;
import com.example.cinemakiosk.dto.ScheduleDTO;
import com.example.cinemakiosk.dto.SeatPolicyDTO;
import com.example.cinemakiosk.dto.TheaterDTO;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TheaterVO {
    private Long no; // 상영관 번호
    private Long policyId; // 좌석정책 FK
    private Long cleanupTime; // 정리시간(분)
    private List<ScheduleVO> schedule; // 1:다

    /**
     * VO -> DTO
     * @param theaterVO
     * @return DTO
     */
    public static TheaterDTO toDTO(TheaterVO theaterVO){

        return TheaterDTO.builder()
                .no(theaterVO.getNo())
                .policyId(theaterVO.getPolicyId())
                .cleanupTime(theaterVO.getCleanupTime())
                .build();
    }
}
