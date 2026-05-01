package com.example.cinemakiosk.vo;

import com.example.cinemakiosk.domain.enums.Grade;
import com.example.cinemakiosk.dto.MemberDTO;
import com.example.cinemakiosk.dto.PointHistoryDTO;
import com.example.cinemakiosk.dto.ReservationDetailsDTO;
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
    private Grade grade; // 회원 등급
    private Integer point; // 포인트
    private LocalDateTime createAt; // 생성일

    /**
     * VO -> DTO
     * @param memberVO
     * @return
     */
    public static MemberDTO toDTO(MemberVO memberVO){
        return MemberDTO.builder()
                .phone(memberVO.getPhone())
                .grade(memberVO.getGrade())
                .point(memberVO.getPoint())
                .createAt(memberVO.getCreateAt())
                .build();
    }
}
