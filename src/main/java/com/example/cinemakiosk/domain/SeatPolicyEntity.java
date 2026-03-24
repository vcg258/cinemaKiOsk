package com.example.cinemakiosk.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Builder
@ToString(exclude = "theater")
@NoArgsConstructor
@AllArgsConstructor
public class SeatPolicyEntity {
    @Column(columnDefinition = "CHAR(36)")
    @Id private String policyId; // 좌석 아이디
    @Column(length = 20)
    private String name; // 좌석 이름
    @Column(columnDefinition = "BIGINT UNSIGNED DEFAULT 0")
    private Long cost; // 좌석 비용

    @OneToMany(mappedBy = "seatPolicyEntity", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<TheaterEntity> theaterEntity;
}
