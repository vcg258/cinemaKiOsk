package com.example.cinemakiosk.dto;

import com.example.cinemakiosk.domain.ScheduleEntity;
import com.example.cinemakiosk.domain.TheaterEntity;
import com.example.cinemakiosk.vo.ScheduleVO;
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
public class TheaterDTO {
    private Long no; // 상영관 번호
    private SeatPolicyDTO seatPolicy; // 좌석 정책 FK
    private Long cleanupTime; // 정리시간(분)
    private List<ScheduleDTO> schedule; // 1:다

    /**
     * DTO -> Entity
     * @param theaterDTO
     * @return Entity
     */
    public static TheaterEntity toEntity(TheaterDTO theaterDTO){
        List<ScheduleDTO> scheduleDTOs = theaterDTO.getSchedule();
        List<ScheduleEntity> scheduleEntitys = new ArrayList<>();

        for (ScheduleDTO scheduleDTO : scheduleDTOs){
            ScheduleEntity scheduleEntity = ScheduleEntity.builder()
                    .id(scheduleDTO.getId())
                    .build();

            scheduleEntitys.add(scheduleEntity);
        }

        return TheaterEntity.builder()
                .no(theaterDTO.getNo())
                .seatPolicyEntity(SeatPolicyDTO.toEntity(theaterDTO.getSeatPolicy()))
                .cleanupTime(theaterDTO.getCleanupTime())
                .scheduleEntity(scheduleEntitys)
                .build();
    }

    /**
     * DTO -> VO
     * @param theaterDTO
     * @return VO
     */
    public static TheaterVO toVO(TheaterDTO theaterDTO){
        List<ScheduleDTO> scheduleDTOs = theaterDTO.getSchedule();
        List<ScheduleVO> scheduleVOs = new ArrayList<>();

        for (ScheduleDTO scheduleDTO : scheduleDTOs){
            ScheduleVO scheduleVO = ScheduleVO.builder()
                    .id(scheduleDTO.getId())
                    .build();

            scheduleVOs.add(scheduleVO);
        }

        return TheaterVO.builder()
                .no(theaterDTO.getNo())
                .seatPolicy(SeatPolicyDTO.toVO(theaterDTO.getSeatPolicy()))
                .cleanupTime(theaterDTO.getCleanupTime())
                .schedule(scheduleVOs)
                .build();
    }
}
