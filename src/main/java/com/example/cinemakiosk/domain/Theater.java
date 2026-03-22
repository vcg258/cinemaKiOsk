package com.example.cinemakiosk.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@ToString(exclude = {"schedule", "seatPolicy"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Theater {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED")
    @Id private Long no; // 상영관 번호
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", nullable = false, foreignKey = @ForeignKey(name = "fk_theater_policy_id"))
    private SeatPolicy seatPolicy; // 좌석 정책 FK
    @Column(columnDefinition = "BIGINT UNSIGNED DEFAULT 0")
    private Long cleanupTime; // 정리시간(분)

    @OneToMany(mappedBy = "theater", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<Schedule> schedule;
}
