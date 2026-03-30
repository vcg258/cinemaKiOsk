package com.example.cinemakiosk.dto;

import com.example.cinemakiosk.domain.BonusPolicyEntity;
import com.example.cinemakiosk.domain.PaymentDetailsEntity;
import com.example.cinemakiosk.vo.BonusPolicyVO;
import com.example.cinemakiosk.vo.PaymentDetailsVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BonusPolicyDTO {
    private Long id;                 // 적립 정책 인덱스
    private String policyName;       // 정책 이름
    private Long giveValue;          // 적립 비율
    private LocalDateTime createAt;  //	시작일
    private LocalDateTime finishedAt;//	만료일
    private Boolean activation;      // 활성화 여부(중요할까?)

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
                .startAt(bonusPolicyDTO.getCreateAt())
                .endAt(bonusPolicyDTO.getFinishedAt())
                .activation(bonusPolicyDTO.getActivation())
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
                .createAt(bonusPolicyDTO.getCreateAt())
                .finishedAt(bonusPolicyDTO.getFinishedAt())
                .activation(bonusPolicyDTO.getActivation())
                .build();
    }
}
