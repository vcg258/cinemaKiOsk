package com.example.cinemakiosk.domain.PaymentDetailsEntity;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDetailsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;             // 인덱스

    @Column(length = 36)
    private String reservationId;  // 예매 정보
    private Long bonusPolicyId;    // 사용한 적립 정책
    @Column(length = 12, nullable = false)
    private String couponNum;      // 사용한 할인 쿠폰, 없는 경우 null
    @Column(nullable = false)
    private Long cost;             // 결제 금액
    @Column(nullable = false)
    private LocalDateTime time;    // 결제 시간
    @Column(columnDefinition = "BIGINT UNSIGNED DEFAULT 0")
    private Long usePoint;         // 사용 포인트 기본값 0
    @Column(nullable = false)
    private Status status;         // ENUM ('PAY','RETURN','FAIL'), 결제 완료, 환불, 실패
}
