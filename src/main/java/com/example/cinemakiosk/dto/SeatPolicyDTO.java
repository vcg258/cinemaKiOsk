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
    private String policyId; // 좌석 아이디
    private String name; // 좌석 이름
    private Long cost; // 좌석 비용
    private List<TheaterDTO> theater; //1:다

    /**
     * DTO -> Entity
     * @param seatPolicyDTO
     * @return Entity
     */
    public static SeatPolicyEntity toEntity(SeatPolicyDTO seatPolicyDTO){
        List<TheaterDTO> theaterDTOs = seatPolicyDTO.getTheater();
        List<TheaterEntity> theaterEntitys = new ArrayList<>();

        for (TheaterDTO theaterDTO : theaterDTOs){
            TheaterEntity theaterEntity = TheaterEntity.builder()
                    .no(theaterDTO.getNo())
                    .build();

            theaterEntitys.add(theaterEntity);
        }

        return SeatPolicyEntity.builder()
                .policyId(seatPolicyDTO.getPolicyId())
                .name(seatPolicyDTO.getName())
                .cost(seatPolicyDTO.getCost())
                .theaterEntity(theaterEntitys)
                .build();
    }

    /**
     * DTO -> VO
     * @param seatPolicyDTO
     * @return VO
     */
    public static SeatPolicyVO toVO(SeatPolicyDTO seatPolicyDTO){
        List<TheaterDTO> theaterDTOs = seatPolicyDTO.getTheater();
        List<TheaterVO> theaterVOs = new ArrayList<>();

        for (TheaterDTO theaterDTO : theaterDTOs){
            TheaterVO theaterVO = TheaterVO.builder()
                    .no(theaterDTO.getNo())
                    .build();

            theaterVOs.add(theaterVO);
        }

        return SeatPolicyVO.builder()
                .policyId(seatPolicyDTO.getPolicyId())
                .name(seatPolicyDTO.getName())
                .cost(seatPolicyDTO.getCost())
                .theater(theaterVOs)
                .build();
    }
}
