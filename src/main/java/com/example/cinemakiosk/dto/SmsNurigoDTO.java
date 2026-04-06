package com.example.cinemakiosk.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class SmsNurigoDTO {
    private String toPhone;
    private String content;
}
