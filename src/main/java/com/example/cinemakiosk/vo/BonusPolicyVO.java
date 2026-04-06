package com.example.cinemakiosk.vo;

import com.example.cinemakiosk.dto.BonusPolicyDTO;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BonusPolicyVO {
    private Long id;                 // 적립 정책 인덱스
    private String policyName;       // 정책 이름
    private Long giveValue;          // 적립 비율
    private LocalDateTime createAt;  //	시작일
    private LocalDateTime finishedAt;//	만료일
    private Boolean activation;      // 활성화 여부(중요할까?)


    /**
     * VO -> DTO
     * @param bonusPolicyVO
     * @return DTO
     */
    public static BonusPolicyDTO toDTO(BonusPolicyVO bonusPolicyVO){
        return BonusPolicyDTO.builder()
                .id(bonusPolicyVO.getId())
                .policyName(bonusPolicyVO.getPolicyName())
                .giveValue(bonusPolicyVO.getGiveValue())
                .createAt(bonusPolicyVO.getCreateAt())
                .finishedAt(bonusPolicyVO.getFinishedAt())
                .activation(bonusPolicyVO.getActivation())
                .build();
    }
}
