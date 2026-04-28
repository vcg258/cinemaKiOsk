package com.example.cinemakiosk.service;

import com.solapi.sdk.SolapiClient;
import com.solapi.sdk.message.exception.SolapiEmptyResponseException;
import com.solapi.sdk.message.exception.SolapiMessageNotReceivedException;
import com.solapi.sdk.message.exception.SolapiUnknownException;
import com.solapi.sdk.message.model.Message;
import com.solapi.sdk.message.service.DefaultMessageService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Random;

@Log4j2
@Service
public class SmsNurigoService {
    @Value("${sms.api.key}")
    private String apiKey;
    @Value("${sms.api.secret}")
    private String apiSecret;
    @Value("${sms.api.phone}")
    private String fromPhone;

    public void sendSms(String toPhone, String content) {
        DefaultMessageService messageService
                = SolapiClient.INSTANCE.createInstance(apiKey, apiSecret);

        Message message = new Message();
        message.setFrom(fromPhone);
        message.setTo(toPhone);
        message.setText(content);

        try {
            messageService.send(message); // 메시지 발송
        } catch (SolapiMessageNotReceivedException e) {
            // 발송에 실패한 메시지 목록을 확인할 수 있습니다
            log.error(e.getFailedMessageList());
            log.error(e.getMessage());
            throw new RuntimeException(e);
        } catch (SolapiEmptyResponseException e) {
            throw new RuntimeException(e);
        } catch (SolapiUnknownException e) {
            throw new RuntimeException(e);
        }

    }

    public void AuthenticationNumber(String toPhone) {
        DefaultMessageService messageService
                = SolapiClient.INSTANCE.createInstance(apiKey, apiSecret);

        Message message = new Message();
        message.setFrom(fromPhone);
        message.setTo(toPhone);


        Random random = new Random();
        // 000,000 ~ 999,999 범위의 숫자 생성
        String randomNumber = String.valueOf(000000 + random.nextInt(999999));

        System.out.println("생성된 6자리 숫자: " + randomNumber);
        message.setText("인증번호 발송: " + randomNumber);

        try {
            messageService.send(message); // 메시지 발송
        } catch (SolapiMessageNotReceivedException e) {
            // 발송에 실패한 메시지 목록을 확인할 수 있습니다
            log.error(e.getFailedMessageList());
            log.error(e.getMessage());
            throw new RuntimeException(e);
        } catch (SolapiEmptyResponseException e) {
            throw new RuntimeException(e);
        } catch (SolapiUnknownException e) {
            throw new RuntimeException(e);
        }
    }
}
