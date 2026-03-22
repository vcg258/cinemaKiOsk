package com.example.cinemakiosk.domain.PointHistory;

import com.example.cinemakiosk.domain.Member;
import com.example.cinemakiosk.domain.TimeBaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@ToString(exclude = {"member"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointHistory extends TimeBaseEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED")
    @Id private Long pointId; // 포인트 인덱스
    // TODO PaymentDetails FK
//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "payment_id", nullable = false, foreignKey = @ForeignKey(name = "fk_point_history_payment_id"))
    @Column(nullable = false, columnDefinition = "CHAR(36)")
    private String paymentId; // 결제 고유번호 FK
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "phone", nullable = false, foreignKey = @ForeignKey(name = "fk_point_history_phone"))
    private Member member; // 회원번호 FK
    @Enumerated(EnumType.STRING) // Enum
    @Column(nullable = false)
    private Type type; // 적립 / 사용 ('EARN', 'USE')
    @Column(nullable = false, columnDefinition = "BIGINT UNSIGNED")
    private Long amountPoint; // 사용할 포인트
//    @Column(nullable = false, columnDefinition = "DATETIME DEFAULT NOW()")
//    private LocalDateTime createAt; // 포인트 변경일
}
