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
    private SeatPolicyVO seatPolicy; // 좌석 정책 FK
    private Long cleanupTime; // 정리시간(분)

    /**
     * VO -> DTO
     * @param theaterVO
     * @return DTO
     */
    public static TheaterDTO toDTO(TheaterVO theaterVO){
        return TheaterDTO.builder()
                .no(theaterVO.getNo())
                .seatPolicy(SeatPolicyVO.toDTO(theaterVO.getSeatPolicy()))
                .cleanupTime(theaterVO.getCleanupTime())
                .build();
    }
}
