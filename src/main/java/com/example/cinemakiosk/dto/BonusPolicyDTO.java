package com.example.cinemakiosk.dto;

import com.example.cinemakiosk.domain.BonusPolicyEntity;
import com.example.cinemakiosk.domain.PaymentDetailsEntity;
import com.example.cinemakiosk.vo.BonusPolicyVO;
import com.example.cinemakiosk.vo.PaymentDetailsVO;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class BonusPolicyDTO {
    private Long id;                 // 적립 정책 인덱스
    private String policyName;       // 정책 이름
    private Long giveValue;          // 적립 비율
    private LocalDateTime createAt;  //	시작일
    private LocalDateTime finishedAt;//	만료일
    private Boolean activation;      // 활성화 여부(중요할까?)
    private List<PaymentDetailsDTO> paymentDetails;

    /**
     * DTO -> Entity
     * @param bonusPolicyDTO
     * @return Entity
     */
    public static BonusPolicyEntity toEntity(BonusPolicyDTO bonusPolicyDTO){
        //OneToMany 변수는 본인 객체를 제외한 값만 받기. 순환참조 방지.
        List<PaymentDetailsDTO> paymentDetailsDTOs = bonusPolicyDTO.getPaymentDetails();
        List<PaymentDetailsEntity> paymentDetailsEntitys = new ArrayList<>();

        if (paymentDetailsDTOs != null) {
            for (PaymentDetailsDTO paymentDetailsDTO : paymentDetailsDTOs) {
                //pk 만 받아오기.
                PaymentDetailsEntity paymentDetailsEntity = PaymentDetailsEntity.builder()
                        .id(paymentDetailsDTO.getId())
                        .build();

                paymentDetailsEntitys.add(paymentDetailsEntity);
            }
        }

        return BonusPolicyEntity.builder()
                .id(bonusPolicyDTO.getId())
                .policyName(bonusPolicyDTO.getPolicyName())
                .giveValue(bonusPolicyDTO.getGiveValue())
                .startAt(bonusPolicyDTO.getCreateAt())
                .endAt(bonusPolicyDTO.getFinishedAt())
                .activation(bonusPolicyDTO.getActivation())
                .paymentDetailsEntity(paymentDetailsEntitys)
                .build();
    }

    /**
     * DTO -> VO
     * @param bonusPolicyDTO
     * @return VO
     */
    public static BonusPolicyVO toVO(BonusPolicyDTO bonusPolicyDTO){
        //OneToMany 변수는 본인 객체를 제외한 값만 받기. 순환참조 방지.
        List<PaymentDetailsDTO> paymentDetailsDTOs = bonusPolicyDTO.getPaymentDetails();
        List<PaymentDetailsVO> paymentDetailsVOs = new ArrayList<>();

        if (paymentDetailsDTOs != null) {
            for (PaymentDetailsDTO paymentDetailsDTO : paymentDetailsDTOs) {
                //pk 만 받아오기.
                PaymentDetailsVO paymentDetailsVO = PaymentDetailsVO.builder()
                        .id(paymentDetailsDTO.getId())
                        .build();

                paymentDetailsVOs.add(paymentDetailsVO);
            }
        }

        return BonusPolicyVO.builder()
                .id(bonusPolicyDTO.getId())
                .policyName(bonusPolicyDTO.getPolicyName())
                .giveValue(bonusPolicyDTO.getGiveValue())
                .createAt(bonusPolicyDTO.getCreateAt())
                .finishedAt(bonusPolicyDTO.getFinishedAt())
                .activation(bonusPolicyDTO.getActivation())
                .paymentDetails(paymentDetailsVOs)
                .build();
    }
}
