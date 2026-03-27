package com.example.cinemakiosk.domain;

import com.example.cinemakiosk.dto.ScheduleDTO;
import com.example.cinemakiosk.dto.SeatPolicyDTO;
import com.example.cinemakiosk.dto.TheaterDTO;
import com.example.cinemakiosk.vo.ScheduleVO;
import com.example.cinemakiosk.vo.TheaterVO;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@ToString(exclude = {"scheduleEntity", "seatPolicyEntity"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "theater")
public class TheaterEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED")
    @Id
    private Long no; // 상영관 번호
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", nullable = false, foreignKey = @ForeignKey(name = "fk_theater_policy_id"))
    private SeatPolicyEntity seatPolicyEntity; // 좌석 정책 FK
    
    @Column(columnDefinition = "BIGINT UNSIGNED DEFAULT 0")
    private Long cleanupTime; // 정리시간(분)

    @OneToMany(mappedBy = "theaterEntity", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<ScheduleEntity> scheduleEntity; // 1:다

    /**
     * Entity -> DTO
     * @param theaterEntity
     * @return DTO
     */
    public static TheaterDTO toDTO(TheaterEntity theaterEntity){
        List<ScheduleEntity> scheduleEntitys = theaterEntity.getScheduleEntity();
        List<ScheduleDTO> scheduleDTOs = new ArrayList<>();

        for (ScheduleEntity scheduleEntity : scheduleEntitys){
            ScheduleDTO scheduleDTO = ScheduleDTO.builder()
                    .id(scheduleEntity.getId())
                    .build();

            scheduleDTOs.add(scheduleDTO);
        }

        return TheaterDTO.builder()
                .no(theaterEntity.getNo())
                .seatPolicy(SeatPolicyEntity.toDTO(theaterEntity.getSeatPolicyEntity()))
                .cleanupTime(theaterEntity.getCleanupTime())
                .schedule(scheduleDTOs)
                .build();
    }
}
