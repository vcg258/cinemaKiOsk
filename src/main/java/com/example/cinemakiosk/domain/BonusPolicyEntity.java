package com.example.cinemakiosk.domain;

import com.example.cinemakiosk.dto.BonusPolicyDTO;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
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
     * 정책 종료 시간 수정 도메인 메서드
     * @param endAt 종료시간 지정
     */
    public void changeEndAt(LocalDateTime endAt) {
        this.endAt = endAt;
    }

    /**
     * 할인정책 만료여부 변경 도메인 메서드
     */
    public void changeActivation(boolean activation) {
        // 만료여부가 같을 경우 return
        if (this.activation == activation) {
            return;
        }
        this.activation = activation;
    }

    /**
     * Entity -> DTO
     * @param bonusPolicyEntity
     * @return DTO
     */
    public static BonusPolicyDTO toDTO(BonusPolicyEntity bonusPolicyEntity){
        return BonusPolicyDTO.builder()
                .id(bonusPolicyEntity.getId())
                .policyName(bonusPolicyEntity.getPolicyName())
                .giveValue(bonusPolicyEntity.getGiveValue())
                .startAt(bonusPolicyEntity.getStartAt())
                .finishedAt(bonusPolicyEntity.getEndAt())
                .activation(bonusPolicyEntity.getActivation())
                .build();
    }
}
