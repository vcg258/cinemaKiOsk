package com.example.cinemakiosk.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MemberDTO {
    private String phone; // 회원 번호
    private Integer point; // 포인트
    private LocalDateTime createAt; // 생성일
}
