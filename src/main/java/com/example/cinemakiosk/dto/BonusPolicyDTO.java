package com.example.cinemakiosk.dto;

import com.example.cinemakiosk.vo.BonusPolicyVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
