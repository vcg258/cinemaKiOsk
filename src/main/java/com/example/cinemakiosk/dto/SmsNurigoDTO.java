package com.example.cinemakiosk.dto;

import com.example.cinemakiosk.domain.TheaterEntity;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class SmsNurigoDTO {

    private Long no; // 상영관 번호 (Theater)
    private String title; // 영화 제목
    private String rating; // 관람등급
    private String startAt; // 시작시간
    private String endAt; // 종료시간
    private Long cost;             // 결제 금액
    private String createAt;    // 결제 시간
    private List<String> seatNumber;     //좌석 번호 (reservation seat)


}
