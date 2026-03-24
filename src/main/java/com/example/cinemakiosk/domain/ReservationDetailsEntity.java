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
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReservationDetailsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(length = 36)
    private String id;                     // 예매 고유번호(uuid)

    @Column(nullable = false, columnDefinition = "BIGINT UNSIGNED")
    private Long scheduleId;               //  스케쥴 정보

    @Column(length = 20, nullable = false)
    private String phone;                  //  회원 번호

    @Column(nullable = false)
    private LocalDateTime createAt; //  예약 시간
}
