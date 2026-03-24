package com.example.cinemakiosk.domain;

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
public class BonusPolicyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED")
    private Long id;                 // 적립 정책 인덱스

    @Column(length = 20, nullable = false)
    private String policyName;       // 정책 이름

    @Column(nullable = false, columnDefinition = "BIGINT UNSIGNED")
    private Long giveValue;          // 적립 비율

    @Column(nullable = false)
    private LocalDateTime createAt;  //	시작일

    @Column(nullable = false)
    private LocalDateTime endAt;//	만료일

    @Column(nullable = false, columnDefinition = "TINYINT DEFAULT 0")
    private Boolean activation;      // 활성화 여부(중요할까?)
}
