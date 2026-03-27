package com.example.cinemakiosk.vo;

import com.example.cinemakiosk.domain.MemberEntity;
import com.example.cinemakiosk.domain.PointHistoryEntity.PointHistoryEntity;
import com.example.cinemakiosk.domain.ReservationDetailsEntity;
import com.example.cinemakiosk.dto.MemberDTO;
import com.example.cinemakiosk.dto.PointHistoryDTO;
import com.example.cinemakiosk.dto.ReservationDetailsDTO;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MemberVO {
    private String phone; // 회원 번호
    private Integer point; // 포인트
    private LocalDateTime createAt; // 생성일
    private List<PointHistoryVO> pointHistories; //1:다
    private List<ReservationDetailsVO> reservationDetails; //1:다

    /**
     * VO -> DTO
     * @param memberVO
     * @return
     */
    public static MemberDTO toDTO(MemberVO memberVO){
        //OneToMany 변수는 본인 객체를 제외한 값만 받기. 순환참조 방지.
        List<PointHistoryVO> pointHistoryVOs = memberVO.getPointHistories();
        List<PointHistoryDTO> pointHistoryDTOs = new ArrayList<>();


        for (PointHistoryVO pointHistoryVO : pointHistoryVOs){
            //pk 만 받아오기.
            PointHistoryDTO pointHistoryDTO = PointHistoryDTO.builder()
                    .pointId(pointHistoryVO.getPointId())
                    .build();

            pointHistoryDTOs.add(pointHistoryDTO);
        }


        List<ReservationDetailsVO> reservationDetailsVOs = memberVO.getReservationDetails();
        List<ReservationDetailsDTO> reservationDetailsDTOs = new ArrayList<>();

        for (ReservationDetailsVO reservationDetailsVO : reservationDetailsVOs){
            ReservationDetailsDTO reservationDetailsDTO = ReservationDetailsDTO.builder()
                    .id(reservationDetailsVO.getId())
                    .build();

            reservationDetailsDTOs.add(reservationDetailsDTO);
        }

        return MemberDTO.builder()
                .phone(memberVO.getPhone())
                .point(memberVO.getPoint())
                .createAt(memberVO.getCreateAt())
                .pointHistories(pointHistoryDTOs)
                .reservationDetails(reservationDetailsDTOs)
                .build();
    }
}
