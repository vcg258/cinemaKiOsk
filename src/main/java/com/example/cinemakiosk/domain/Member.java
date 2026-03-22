package com.example.cinemakiosk.domain;

import com.example.cinemakiosk.domain.PointHistory.PointHistory;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Builder
@ToString(exclude = "pointHistory")
@NoArgsConstructor
@AllArgsConstructor
public class Member extends TimeBaseEntity {
    @Column(length = 20)
    @Id private String phone; // 회원 번호
    @Column(columnDefinition = "INT UNSIGNED DEFAULT 0")
    private Integer point; // 포인트
//    @Column(nullable = false, columnDefinition = "DATETIME DEFAULT NOW()")
//    private LocalDateTime createAt; // 생성일

    @OneToMany(mappedBy = "member", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<PointHistory> pointHistory;
}