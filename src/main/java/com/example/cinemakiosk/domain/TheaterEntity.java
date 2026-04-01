package com.example.cinemakiosk.domain;

import com.example.cinemakiosk.dto.ScheduleDTO;
import com.example.cinemakiosk.dto.SeatPolicyDTO;
import com.example.cinemakiosk.dto.TheaterDTO;
import com.example.cinemakiosk.vo.ScheduleVO;
import com.example.cinemakiosk.vo.TheaterVO;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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

    @OnDelete(action= OnDeleteAction.CASCADE)
    @OneToMany(mappedBy = "theaterEntity", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<ScheduleEntity> scheduleEntity; // 1:다

    /**
     * 상영관 좌석정책 업데이트 도메인 메서드
     * @param entity 좌석정책 변경할때 사용
     */
    public void changeSeatPolicy(SeatPolicyEntity entity) {
        this.seatPolicyEntity = entity;
    }

    /**
     * 상영관 정리시간 업데이트 도메인 메서드
     * @param cleanupTime 정리시간 수정시 사용
     */
    public void changeCleantime(Long cleanupTime) {
        this.cleanupTime = cleanupTime;
    }

    /**
     * Entity -> DTO
     * @param theaterEntity
     * @return DTO
     */
    public static TheaterDTO toDTO(TheaterEntity theaterEntity){

        return TheaterDTO.builder()
                .no(theaterEntity.getNo())
                .policyId(theaterEntity.getSeatPolicyEntity().getPolicyId())
                .cleanupTime(theaterEntity.getCleanupTime())
                .build();
    }
}
