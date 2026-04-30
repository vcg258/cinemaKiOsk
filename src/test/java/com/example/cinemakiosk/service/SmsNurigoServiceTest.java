package com.example.cinemakiosk.service;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Random;

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

    @Test
    public void testSendSms_toPhone() {
        Random random = new Random();
        // 000,000 ~ 999,999 범위의 숫자 생성
        int randomNumber = 000000 + random.nextInt(900000);

        System.out.println("생성된 6자리 숫자: " + randomNumber);
    }

    @Test
    public void receiveSms() {
        smsNurigoService.receipt("01049393069", "11111111-1111-1111-1111-111111111111");
    }
}
