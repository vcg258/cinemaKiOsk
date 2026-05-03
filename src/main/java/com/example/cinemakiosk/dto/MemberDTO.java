package com.example.cinemakiosk.dto;

import com.example.cinemakiosk.domain.MemberEntity;
import com.example.cinemakiosk.domain.PointHistoryEntity;
import com.example.cinemakiosk.domain.ReservationDetailsEntity;
import com.example.cinemakiosk.domain.enums.Grade;
import com.example.cinemakiosk.vo.MemberVO;
import com.example.cinemakiosk.vo.PointHistoryVO;
import com.example.cinemakiosk.vo.ReservationDetailsVO;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDTO {
    private String phone; // 회원 번호
    private Grade grade; // 회원 등급
    private Integer point; // 포인트
    private LocalDateTime createAt; // 생성일

    /**
     * DTO -> Entity
     * @param memberDTO
     * @return Entity
     */
    public static MemberEntity toEntity(MemberDTO memberDTO){
        if (memberDTO == null) return null;
        return MemberEntity.builder()
                .phone(memberDTO.getPhone())
                .grade(memberDTO.getGrade())
                .point(memberDTO.getPoint())
                .createAt(memberDTO.getCreateAt())
                .build();
    }

    /**
     * DTO -> VO
     * @param memberDTO
     * @return VO
     */
    public static MemberVO toVO(MemberDTO memberDTO){
        if (memberDTO == null) return null;
        return MemberVO.builder()
                .phone(memberDTO.getPhone())
                .grade(memberDTO.getGrade())
                .point(memberDTO.getPoint())
                .createAt(memberDTO.getCreateAt())
                .build();
    }
}
