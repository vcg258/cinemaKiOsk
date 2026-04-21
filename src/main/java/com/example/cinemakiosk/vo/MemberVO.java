package com.example.cinemakiosk.vo;

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
    private Integer point; // 포인트
    private LocalDateTime createAt; // 생성일

    /**
     * VO -> DTO
     * @param memberVO
     * @return
     */
    public static MemberDTO toDTO(MemberVO memberVO){
        if (memberVO == null) return null;

        return MemberDTO.builder()
                .phone(memberVO.getPhone())
                .point(memberVO.getPoint())
                .createAt(memberVO.getCreateAt())
                .build();
    }
}
