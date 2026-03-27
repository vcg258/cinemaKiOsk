package com.example.cinemakiosk.domain.PointHistoryEntity;

import com.example.cinemakiosk.domain.MemberEntity;
import com.example.cinemakiosk.domain.PaymentDetailsEntity.PaymentDetailsEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@ToString(exclude = {"memberEntity", "paymentDetailsEntity"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "point_history")
public class PointHistoryEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED")
    @Id private Long pointId; // 포인트 인덱스

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false, foreignKey = @ForeignKey(name = "fk_point_history_payment_id"))
    private PaymentDetailsEntity paymentDetailsEntity; // 결제 고유번호 FK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "phone", nullable = false, foreignKey = @ForeignKey(name = "fk_point_history_phone"))
    private MemberEntity memberEntity; // 회원번호 FK

    @Enumerated(EnumType.STRING) // Enum
    @Column(nullable = false)
    private Type type; // 적립 / 사용 / 환불 ('EARN', 'USE', 'REFUND')

    @Column(nullable = false, columnDefinition = "INT UNSIGNED")
    private Integer amountPoint; // 사용할 포인트

    @CreatedDate
    @Column(nullable = false, updatable = false, columnDefinition = "DATETIME DEFAULT NOW()")
    private LocalDateTime createAt; // 포인트 변경일
}
