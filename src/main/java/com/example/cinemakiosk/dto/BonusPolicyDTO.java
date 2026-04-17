package com.example.cinemakiosk.dto;

import com.example.cinemakiosk.domain.BonusPolicyEntity;
import com.example.cinemakiosk.vo.BonusPolicyVO;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class BonusPolicyDTO {
    private Long id;                 // 적립 정책 인덱스
    private String policyName;       // 정책 이름
    private Long giveValue;          // 적립 비율
    private LocalDateTime startAt;  //	시작일
    private LocalDateTime endAt;//	만료일
    private boolean activation;      // 활성화 여부(중요할까?)

    /**
     * DTO -> Entity
     * @param bonusPolicyDTO
     * @return Entity
     */
    public static BonusPolicyEntity toEntity(BonusPolicyDTO bonusPolicyDTO){
        return BonusPolicyEntity.builder()
                .id(bonusPolicyDTO.getId())
                .policyName(bonusPolicyDTO.getPolicyName())
                .giveValue(bonusPolicyDTO.getGiveValue())
                .startAt(bonusPolicyDTO.getStartAt())
                .endAt(bonusPolicyDTO.getEndAt())
                .activation(bonusPolicyDTO.isActivation())
                .build();
    }

    /**
     * DTO -> VO
     * @param bonusPolicyDTO
     * @return VO
     */
    public static BonusPolicyVO toVO(BonusPolicyDTO bonusPolicyDTO){
        return BonusPolicyVO.builder()
                .id(bonusPolicyDTO.getId())
                .policyName(bonusPolicyDTO.getPolicyName())
                .giveValue(bonusPolicyDTO.getGiveValue())
                .startAt(bonusPolicyDTO.getStartAt())
                .endAt(bonusPolicyDTO.getEndAt())
                .activation(bonusPolicyDTO.isActivation())
                .build();
    }
}
