package com.example.cinemakiosk.vo;

import com.example.cinemakiosk.domain.BonusPolicyEntity;
import com.example.cinemakiosk.domain.PaymentDetailsEntity.PaymentDetailsEntity;
import com.example.cinemakiosk.dto.BonusPolicyDTO;
import com.example.cinemakiosk.dto.PaymentDetailsDTO;
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
public class BonusPolicyVO {
    private Long id;                 // 적립 정책 인덱스
    private String policyName;       // 정책 이름
    private Long giveValue;          // 적립 비율
    private LocalDateTime createAt;  //	시작일
    private LocalDateTime finishedAt;//	만료일
    private Boolean activation;      // 활성화 여부(중요할까?)
    private List<PaymentDetailsVO> paymentDetails;


    /**
     * VO -> DTO
     * @param bonusPolicyVO
     * @return DTO
     */
    public static BonusPolicyDTO toDTO(BonusPolicyVO bonusPolicyVO){
        //OneToMany 변수는 본인 객체를 제외한 값만 받기. 순환참조 방지.
        List<PaymentDetailsVO> paymentDetailsVOs = bonusPolicyVO.getPaymentDetails();
        List<PaymentDetailsDTO> paymentDetailsDTOs = new ArrayList<>();


        for (PaymentDetailsVO paymentDetailsVO : paymentDetailsVOs){
            //pk 만 받아오기.
            PaymentDetailsDTO paymentDetailsDTO = PaymentDetailsDTO.builder()
                    .id(paymentDetailsVO.getId())
                    .build();

            paymentDetailsDTOs.add(paymentDetailsDTO);
        }

        return BonusPolicyDTO.builder()
                .id(bonusPolicyVO.getId())
                .policyName(bonusPolicyVO.getPolicyName())
                .giveValue(bonusPolicyVO.getGiveValue())
                .createAt(bonusPolicyVO.getCreateAt())
                .finishedAt(bonusPolicyVO.getFinishedAt())
                .activation(bonusPolicyVO.getActivation())
                .paymentDetails(paymentDetailsDTOs)
                .build();
    }
}
