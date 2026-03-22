package com.example.cinemakiosk.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MemberVO {
    private String phone; // 회원 번호
    private Integer point; // 포인트
    private LocalDateTime createAt; // 생성일
}
