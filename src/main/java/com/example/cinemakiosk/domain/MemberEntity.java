package com.example.cinemakiosk.domain;

import com.example.cinemakiosk.dto.MemberDTO;
import com.example.cinemakiosk.dto.PointHistoryDTO;
import com.example.cinemakiosk.dto.ReservationDetailsDTO;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@ToString(exclude = {"pointHistoryEntity", "reservationDetailsEntity"})
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "member")
public class MemberEntity{
    @Column(length = 20)
    @Id private String phone; // 회원 번호
    @Column(columnDefinition = "INT UNSIGNED DEFAULT 0")
    private Integer point; // 포인트
    @Column(nullable = false, columnDefinition = "DATETIME DEFAULT NOW()")
    private LocalDateTime createAt; // 생성일

    @OneToMany(mappedBy = "memberEntity", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<PointHistoryEntity> pointHistoryEntity;
    @OneToMany(mappedBy = "memberEntity", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<ReservationDetailsEntity> reservationDetailsEntity;

    public void setPoint(int point){
        this.point = point;
    }

    /**
     * Entity -> DTO
     * @param memberEntity
     * @return DTO
     */
    public static MemberDTO toDTO(MemberEntity memberEntity){
        //OneToMany 변수는 본인 객체를 제외한 값만 받기. 순환참조 방지.
        List<PointHistoryEntity> pointHistoryEntitys = memberEntity.getPointHistoryEntity();
        List<PointHistoryDTO> pointHistoryDTOs = new ArrayList<>();


        for (PointHistoryEntity pointHistoryEntity : pointHistoryEntitys){
            //pk 만 받아오기.
            PointHistoryDTO pointHistoryDTO = PointHistoryDTO.builder()
                    .pointId(pointHistoryEntity.getPointId())
                    .build();

            pointHistoryDTOs.add(pointHistoryDTO);
        }


        List<ReservationDetailsEntity> reservationDetailsEntitys = memberEntity.getReservationDetailsEntity();
        List<ReservationDetailsDTO> reservationDetailsDTOs = new ArrayList<>();

        for (ReservationDetailsEntity reservationDetailsEntity : reservationDetailsEntitys){
            ReservationDetailsDTO reservationDetailsDTO = ReservationDetailsDTO.builder()
                    .id(reservationDetailsEntity.getId())
                    .build();

            reservationDetailsDTOs.add(reservationDetailsDTO);
        }

        return MemberDTO.builder()
                .phone(memberEntity.getPhone())
                .point(memberEntity.getPoint())
                .createAt(memberEntity.getCreateAt())
                .pointHistories(pointHistoryDTOs)
                .reservationDetails(reservationDetailsDTOs)
                .build();
    }
}