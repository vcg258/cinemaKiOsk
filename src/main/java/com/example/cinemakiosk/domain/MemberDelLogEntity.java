package com.example.cinemakiosk.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@Entity
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "member_cleanup_log")
public class MemberDelLogEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED")
    @Id private Long memberId; // 인덱스

    @OneToOne
    @JoinColumn(name = "phone", nullable = true, foreignKey = @ForeignKey(name = "fk_member_cleanup_log_member_phone"))
    private MemberEntity memberEntity; // 회원 번호

    @Column(nullable = false, columnDefinition = "INT UNSIGNED DEFAULT 0")
    private int point; // 포인트

    @Column(nullable = false)
    private LocalDateTime createAt; // 생성일

    @Column(columnDefinition = "DATETIME DEFAULT NOW()")
    private LocalDateTime deleteAt; // 삭제일
}
