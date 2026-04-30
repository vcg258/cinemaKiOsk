package com.example.cinemakiosk.service;

import com.example.cinemakiosk.domain.enums.AuthResult;

public interface SmsNurigoService {
    // 내용을 정해서 문자를 발송하는 메서드
    void sendSms(String toPhone, String content);

    // 인증번호 발송
    void AuthenticationNumber(String toPhone);

    // 인증번호 검증
    AuthResult comparison(String toPhone, String inputCode);

    // 영수증
    void receipt (String toPhone, String uuid);
}
