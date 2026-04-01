package com.example.cinemakiosk.service;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Log4j2
@SpringBootTest
class SmsNurigoServiceTest {
    @Autowired
    private SmsNurigoService smsNurigoService;

    @Test
    public void testSendSms() {
        String toPhone = "01049393069"; // 메시지 받을 번호
        String content = "발송 테스트";
        smsNurigoService.sendSms(toPhone, content);
    }
}