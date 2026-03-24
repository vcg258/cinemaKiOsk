package com.example.cinemakiosk.vo;

import lombok.*;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CouponVO {
    private String couponNum; // 쿠폰 번호
    private Long policyId; // 할인 정책 인덱스 FK
    private boolean status; // 사용여부 (사용가능 = true, 불가능 = false)
}
