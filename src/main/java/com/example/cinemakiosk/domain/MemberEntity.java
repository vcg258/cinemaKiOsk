package com.example.cinemakiosk.domain;

import com.example.cinemakiosk.domain.PointHistoryEntity.PointHistoryEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Builder
@ToString(exclude = {"pointHistoryEntity", "reservationDetailsEntity"})
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "member")
public class MemberEntity extends TimeBaseEntity{
    @Column(length = 20)
    @Id private String phone; // 회원 번호
    @Column(columnDefinition = "INT UNSIGNED DEFAULT 0")
    private Integer point; // 포인트
//    @Column(nullable = false, columnDefinition = "DATETIME DEFAULT NOW()")
//    private LocalDateTime createAt; // 생성일

    @OneToMany(mappedBy = "memberEntity", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<PointHistoryEntity> pointHistoryEntity;
    @OneToMany(mappedBy = "memberEntity", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<ReservationDetailsEntity> reservationDetailsEntity;

    /**
     * 포인트 업데이트 Setter
     * @param amount 변경포인트
     */
    public void changePoint(Integer amount) {
        this.point = amount;
    }
}