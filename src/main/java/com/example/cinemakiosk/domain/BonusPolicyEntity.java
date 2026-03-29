package com.example.cinemakiosk.domain;

import com.example.cinemakiosk.dto.BonusPolicyDTO;
import com.example.cinemakiosk.dto.PaymentDetailsDTO;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@ToString(exclude = {"paymentDetailsEntity"})
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bonus_policy")
public class BonusPolicyEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED")
    private Long id;                 // 적립 정책 인덱스

    @Column(length = 20, nullable = false)
    private String policyName;       // 정책 이름

    @Column(nullable = false, columnDefinition = "BIGINT UNSIGNED")
    private Long giveValue;          // 적립 비율

    @Column(nullable = false)
    private LocalDateTime startAt;  //	시작일

    @Column(nullable = false)
    private LocalDateTime endAt;//	만료일

    @Column(nullable = false, columnDefinition = "TINYINT DEFAULT 0")
    private Boolean activation;      // 활성화 여부(중요할까?)

    @OnDelete(action= OnDeleteAction.CASCADE)
    @OneToMany(mappedBy = "bonusPolicyEntity", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<PaymentDetailsEntity> paymentDetailsEntity;

    /**
     * Entity -> DTO
     * @param bonusPolicyEntity
     * @return DTO
     */
    public static BonusPolicyDTO toDTO(BonusPolicyEntity bonusPolicyEntity){
        //OneToMany 변수는 본인 객체를 제외한 값만 받기. 순환참조 방지.
        List<PaymentDetailsEntity> paymentDetailsEntities = bonusPolicyEntity.getPaymentDetailsEntity();
        List<PaymentDetailsDTO> paymentDetailsDTOs = new ArrayList<>();


        for (PaymentDetailsEntity paymentDetailsEntity : paymentDetailsEntities){
            //pk 만 받아오기.
            PaymentDetailsDTO paymentDetailsDTO = PaymentDetailsDTO.builder()
                    .id(paymentDetailsEntity.getId())
                    .build();

            paymentDetailsDTOs.add(paymentDetailsDTO);
        }

        return BonusPolicyDTO.builder()
                .id(bonusPolicyEntity.getId())
                .policyName(bonusPolicyEntity.getPolicyName())
                .giveValue(bonusPolicyEntity.getGiveValue())
                .createAt(bonusPolicyEntity.getStartAt())
                .finishedAt(bonusPolicyEntity.getEndAt())
                .activation(bonusPolicyEntity.getActivation())
                .paymentDetails(paymentDetailsDTOs)
                .build();
    }
}
