package com.example.cinemakiosk.service;

import com.example.cinemakiosk.domain.enums.AuthResult;
import com.solapi.sdk.SolapiClient;
import com.solapi.sdk.message.exception.SolapiEmptyResponseException;
import com.solapi.sdk.message.exception.SolapiMessageNotReceivedException;
import com.solapi.sdk.message.exception.SolapiUnknownException;
import com.solapi.sdk.message.model.Message;
import com.solapi.sdk.message.service.DefaultMessageService;
import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Log4j2
@Service
public class SmsNurigoService {
    @Value("${sms.api.key}")
    private String apiKey;
    @Value("${sms.api.secret}")
    private String apiSecret;
    @Value("${sms.api.phone}")
    private String fromPhone;

    // 인증번호 입력받는 Map
    private static final Map<String, String> authentication = new ConcurrentHashMap<>();
    // 인증번호 제한시간확인용 Map
    private static final Map<String, LocalDateTime> expiredAt = new ConcurrentHashMap<>();

    private DefaultMessageService messageService;

    // Value 주입후 생성하기 위해
    @PostConstruct
    public void init() {
        this.messageService = SolapiClient.INSTANCE.createInstance(apiKey, apiSecret);
    }

    /**
     * 폰 번호와 내용입력시 폰 번호에 내용 메시지 보냄
     * @param toPhone
     * @param content
     */
    public void sendSms(String toPhone, String content) {

        messageService = SolapiClient.INSTANCE.createInstance(apiKey, apiSecret);
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

    /**
     * 폰 번호 입력시 그 번호에 랜덤 6자리 숫자 발송
     * @param toPhone
     */
    public void AuthenticationNumber(String toPhone) {
        if (toPhone == null) {
            throw new IllegalArgumentException("toPhone이(가) null입니다.");
        }

        Message message = new Message();

        // 보내는 번호
        message.setFrom(fromPhone);
        // 받는 번호(고객)
        message.setTo(toPhone);


        // 000,000 ~ 999,999 범위의 숫자 생성
        SecureRandom secureRandom = new SecureRandom();
        String randomNumber = String.format("%06d", secureRandom.nextInt(999999));

        log.info("생성된 6자리 숫자: " + randomNumber);

        // 고객에게 보내는 메시지 내용
        message.setText("인증번호 발송: " + randomNumber);

        // Map에 저장
        authentication.put(toPhone, randomNumber);

        // 제한시간용 Map (3분)
        expiredAt.put(toPhone, LocalDateTime.now().plusMinutes(3));

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


    /**
     * 인증번호 검증
     * @param toPhone
     * @param inputCode
     * @return
     */
    public AuthResult comparison(String toPhone, String inputCode) {
        // null 체크
        if (inputCode == null) {
            throw new IllegalArgumentException("inputCode가 null입니다.");
        }

        LocalDateTime expiry = expiredAt.get(toPhone);

        // 6자리가 아닌 경우
        if (inputCode.length() != 6) {
            return AuthResult.INVALID_FORMAT;
        }

        // 입력값이 틀릴 경우
        if (!authentication.get(toPhone).equals(inputCode)) {
            return AuthResult.MISMATCH;
        }

        // 시간이 만료된 경우 (Map 삭제)
        if (expiry == null || LocalDateTime.now().isAfter(expiry)) {
            authentication.remove(toPhone);
            expiredAt.remove(toPhone);
            return AuthResult.EXPIRED;
        }

        // 입력값이 맞을 경우 (Map 삭제)
        authentication.remove(toPhone);
        expiredAt.remove(toPhone);
        return AuthResult.SUCCESS;

    }
}
