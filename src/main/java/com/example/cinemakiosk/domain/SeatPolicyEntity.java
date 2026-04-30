package com.example.cinemakiosk.domain;

import com.example.cinemakiosk.dto.SeatPolicyDTO;
import com.example.cinemakiosk.dto.TheaterDTO;
import com.example.cinemakiosk.vo.SeatPolicyVO;
import com.example.cinemakiosk.vo.TheaterVO;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@ToString(exclude = "theaterEntity")
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "seat_policy")
public class SeatPolicyEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED")
    @Id private Long policyId; // 좌석 아이디
    @Column(length = 20)
    private String name; // 좌석 이름
    @Column(columnDefinition = "BIGINT UNSIGNED DEFAULT 0")
    private Long cost; // 좌석 비용

    @OneToMany(mappedBy = "seatPolicyEntity")
    private List<TheaterEntity> theaterEntity; //1:다

    /**
     * 좌석정책 업데이트를 위한 도메인 메서드
     * @param name 수정 이름
     * @param cost 수정 비용
     */
    public void updateSeatPolicy(String name, Long cost) {
        this.name = name;
        this.cost = cost;
    }

    /**
     * Entity -> DTO
     * @param seatPolicyEntity
     * @return DTO
     */
    public static SeatPolicyDTO toDTO(SeatPolicyEntity seatPolicyEntity){
        return SeatPolicyDTO.builder()
                .policyId(seatPolicyEntity.getPolicyId())
                .name(seatPolicyEntity.getName())
                .cost(seatPolicyEntity.getCost())
                .build();
    }
}
