package com.example.cinemakiosk.dto;

import com.example.cinemakiosk.domain.MemberEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberDTO {
    private String phone; // 회원 번호
    private Integer point; // 포인트

    /**
     * DTO -> Entity
     * @param memberDTO DTO
     * @return Entity
     */
    public static MemberEntity toEntity(MemberDTO memberDTO) {
        return MemberEntity.builder()
                .phone(memberDTO.getPhone())
                .point(memberDTO.getPoint())
                .build();
    }
}
