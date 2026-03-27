package com.example.cinemakiosk.dto;

import com.example.cinemakiosk.domain.MemberEntity;
import com.example.cinemakiosk.domain.PointHistoryEntity.PointHistoryEntity;
import com.example.cinemakiosk.domain.ReservationDetailsEntity;
import com.example.cinemakiosk.vo.MemberVO;
import com.example.cinemakiosk.vo.PointHistoryVO;
import com.example.cinemakiosk.vo.ReservationDetailsVO;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MemberDTO {
    private String phone; // 회원 번호
    private Integer point; // 포인트
    private LocalDateTime createAt; // 생성일
    private List<PointHistoryDTO> pointHistories; //1:다
    private List<ReservationDetailsDTO> reservationDetails; //1:다

    /**
     * DTO -> Entity
     * @param memberDTO
     * @return Entity
     */
    public static MemberEntity toEntity(MemberDTO memberDTO){
        //OneToMany 변수는 본인 객체를 제외한 값만 받기. 순환참조 방지.
        List<PointHistoryDTO> pointHistoryDTOs = memberDTO.getPointHistories();
        List<PointHistoryEntity> pointHistoryEntitys = new ArrayList<>();


        for (PointHistoryDTO pointHistoryDTO : pointHistoryDTOs){
            //pk 만 받아오기.
            PointHistoryEntity pointHistoryEntity = PointHistoryEntity.builder()
                    .pointId(pointHistoryDTO.getPointId())
                    .build();

            pointHistoryEntitys.add(pointHistoryEntity);
        }


        List<ReservationDetailsDTO> reservationDetailsDTOs = memberDTO.getReservationDetails();
        List<ReservationDetailsEntity> reservationDetailsEntitys = new ArrayList<>();

        for (ReservationDetailsDTO reservationDetailsDTO : reservationDetailsDTOs){
            ReservationDetailsEntity reservationDetailsEntity = ReservationDetailsEntity.builder()
                    .id(reservationDetailsDTO.getId())
                    .build();

            reservationDetailsEntitys.add(reservationDetailsEntity);
        }

        return MemberEntity.builder()
                .phone(memberDTO.getPhone())
                .point(memberDTO.getPoint())
                .createAt(memberDTO.getCreateAt())
                .pointHistoryEntity(pointHistoryEntitys)
                .reservationDetailsEntity(reservationDetailsEntitys)
                .build();
    }

    /**
     * DTO -> VO
     * @param memberDTO
     * @return VO
     */
    public static MemberVO toVO(MemberDTO memberDTO){
        //OneToMany 변수는 본인 객체를 제외한 값만 받기. 순환참조 방지.
        List<PointHistoryDTO> pointHistoryDTOs = memberDTO.getPointHistories();
        List<PointHistoryVO> pointHistoryVOs = new ArrayList<>();


        for (PointHistoryDTO pointHistoryDTO : pointHistoryDTOs){
            //pk 만 받아오기.
            PointHistoryVO pointHistoryVO = PointHistoryVO.builder()
                    .pointId(pointHistoryDTO.getPointId())
                    .build();

            pointHistoryVOs.add(pointHistoryVO);
        }


        List<ReservationDetailsDTO> reservationDetailsDTOs = memberDTO.getReservationDetails();
        List<ReservationDetailsVO> reservationDetailsVOs = new ArrayList<>();

        for (ReservationDetailsDTO reservationDetailsDTO : reservationDetailsDTOs){
            ReservationDetailsVO reservationDetailsVO = ReservationDetailsVO.builder()
                    .id(reservationDetailsDTO.getId())
                    .build();

            reservationDetailsVOs.add(reservationDetailsVO);
        }

        return MemberVO.builder()
                .phone(memberDTO.getPhone())
                .point(memberDTO.getPoint())
                .createAt(memberDTO.getCreateAt())
                .pointHistories(pointHistoryVOs)
                .reservationDetails(reservationDetailsVOs)
                .build();
    }
}
