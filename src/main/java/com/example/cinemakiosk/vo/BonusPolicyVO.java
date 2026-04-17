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
    private LocalDateTime startAt;  //	시작일
    private LocalDateTime endAt;//	만료일
    private boolean activation;      // 활성화 여부(중요할까?)


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
                .startAt(bonusPolicyVO.getStartAt())
                .endAt(bonusPolicyVO.getEndAt())
                .activation(bonusPolicyVO.isActivation())
                .build();
    }
}
