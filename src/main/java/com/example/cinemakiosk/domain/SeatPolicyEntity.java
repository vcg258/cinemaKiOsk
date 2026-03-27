package com.example.cinemakiosk.domain;

import com.example.cinemakiosk.dto.SeatPolicyDTO;
import com.example.cinemakiosk.dto.TheaterDTO;
import com.example.cinemakiosk.vo.SeatPolicyVO;
import com.example.cinemakiosk.vo.TheaterVO;
import jakarta.persistence.*;
import lombok.*;

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
    @Column(columnDefinition = "CHAR(36)")
    @Id private String policyId; // 좌석 아이디
    @Column(length = 20)
    private String name; // 좌석 이름
    @Column(columnDefinition = "BIGINT UNSIGNED DEFAULT 0")
    private Long cost; // 좌석 비용

    @OneToMany(mappedBy = "seatPolicyEntity", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<TheaterEntity> theaterEntity; //1:다

    /**
     * Entity -> DTO
     * @param seatPolicyEntity
     * @return DTO
     */
    public static SeatPolicyDTO toDTO(SeatPolicyEntity seatPolicyEntity){
        List<TheaterEntity> theaterEntitys = seatPolicyEntity.getTheaterEntity();
        List<TheaterDTO> theaterDTOs = new ArrayList<>();

        for (TheaterEntity theaterEntity : theaterEntitys){
            TheaterDTO theaterDTO = TheaterDTO.builder()
                    .no(theaterEntity.getNo())
                    .build();

            theaterDTOs.add(theaterDTO);
        }

        return SeatPolicyDTO.builder()
                .policyId(seatPolicyEntity.getPolicyId())
                .name(seatPolicyEntity.getName())
                .cost(seatPolicyEntity.getCost())
                .theater(theaterDTOs)
                .build();
    }
}
