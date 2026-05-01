package com.example.cinemakiosk.domain;

import com.example.cinemakiosk.domain.enums.Grade;
import com.example.cinemakiosk.dto.MemberDTO;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Builder
@ToString(exclude = {"pointHistoryEntity", "reservationDetailsEntity"})
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "member")
public class MemberEntity{
    @Column(length = 20)
    @Id private String phone; // 회원 번호

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Grade grade; // 회원 등급

    @Column(nullable = false, columnDefinition = "INT UNSIGNED DEFAULT 0")
    private Integer point; // 포인트

    @Column(nullable = false, columnDefinition = "DATETIME DEFAULT NOW()")
    private LocalDateTime createAt; // 생성일

    @OneToMany(mappedBy = "memberEntity", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<PointHistoryEntity> pointHistoryEntity;

    @OneToMany(mappedBy = "memberEntity", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<ReservationDetailsEntity> reservationDetailsEntity;

    public void changeGrade(Grade grade){
        this.grade = grade;
    }

    public void changePoint(int point){
        this.point = point;
    }

    /**
     * Entity -> DTO
     * @param memberEntity
     * @return DTO
     */
    public static MemberDTO toDTO(MemberEntity memberEntity){

        return MemberDTO.builder()
                .phone(memberEntity.getPhone())
                .grade(memberEntity.getGrade())
                .point(memberEntity.getPoint())
                .createAt(memberEntity.getCreateAt())
                .build();
    }
}