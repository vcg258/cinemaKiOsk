package com.example.cinemakiosk.vo;

import com.example.cinemakiosk.domain.SeatPolicyEntity;
import com.example.cinemakiosk.domain.TheaterEntity;
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
public class SeatPolicyVO {
    private String policyId; // 좌석 아이디
    private String name; // 좌석 이름
    private Long cost; // 좌석 비용
    private List<TheaterVO> theater; //1:다

    /**
     * VO -> DTO
     * @param seatPolicyVO
     * @return DTO
     */
    public static SeatPolicyDTO toDTO(SeatPolicyVO seatPolicyVO){
        List<TheaterVO> theaterVOs = seatPolicyVO.getTheater();
        List<TheaterDTO> theaterDTOs = new ArrayList<>();

        for (TheaterVO theaterVO : theaterVOs){
            TheaterDTO theaterDTO = TheaterDTO.builder()
                    .no(theaterVO.getNo())
                    .build();

            theaterDTOs.add(theaterDTO);
        }

        return SeatPolicyDTO.builder()
                .policyId(seatPolicyVO.getPolicyId())
                .name(seatPolicyVO.getName())
                .cost(seatPolicyVO.getCost())
                .theater(theaterDTOs)
                .build();
    }
}
