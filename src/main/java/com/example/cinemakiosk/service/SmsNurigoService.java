package com.example.cinemakiosk.service;

import com.example.cinemakiosk.domain.MovieEntity;
import com.example.cinemakiosk.domain.PaymentDetailsEntity;
import com.example.cinemakiosk.domain.enums.AuthResult;
import com.example.cinemakiosk.domain.enums.Rating;
import com.example.cinemakiosk.domain.enums.Status;
import com.example.cinemakiosk.dto.PaymentDetailsDTO;
import com.example.cinemakiosk.dto.ReservationDetailsDTO;
import com.example.cinemakiosk.dto.SmsNurigoDTO;
import com.example.cinemakiosk.mapper.ReservationDetailsMapper;
import com.example.cinemakiosk.repository.PaymentDetailsRepository;
import com.example.cinemakiosk.vo.ReservationDetailsVO;
import com.solapi.sdk.SolapiClient;
import com.solapi.sdk.message.exception.SolapiEmptyResponseException;
import com.solapi.sdk.message.exception.SolapiMessageNotReceivedException;
import com.solapi.sdk.message.exception.SolapiUnknownException;
import com.solapi.sdk.message.model.Message;
import com.solapi.sdk.message.service.DefaultMessageService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Log4j2
@Service
@RequiredArgsConstructor
public class SmsNurigoService {


    private final ReservationDetailsMapper reservationDetailsMapper;
    private final PaymentDetailsRepository paymentDetailsRepository;

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
        log.info("expire: {}", expiredAt.toString());

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
     * @return AuthResult (enum)
     */
    public AuthResult comparison(String toPhone, String inputCode) {

        // 인증번호가 없는 경우
        if (authentication.get(toPhone) == null) {
            return AuthResult.None;
        }

        LocalDateTime expiry = expiredAt.get(toPhone);
        // 시간이 만료된 경우 (Map 삭제)
        if (LocalDateTime.now().isAfter(expiry)) {
            log.info("expiry: {}", expiry);
            authentication.remove(toPhone);
            expiredAt.remove(toPhone);
            return AuthResult.EXPIRED;
        }



        // 6자리가 아닌 경우
        if (inputCode.length() != 6) {
            return AuthResult.INVALID_FORMAT;
        }

        // 입력값이 틀릴 경우
        if (!authentication.get(toPhone).equals(inputCode)) {
            return AuthResult.MISMATCH;
        }


        // 입력값이 맞을 경우 (Map 삭제)
        authentication.remove(toPhone);
        expiredAt.remove(toPhone);
        return AuthResult.SUCCESS;
    }

    /**
     * 영수증
     * @param no 예매내역 PK
     * @param uuid 결제내역 PK
     */
    public void receipt (String toPhone, String no, String uuid) {
        // localdatetime  읽기 편하게 변환
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        // ReservationDetails 들고오기
        ReservationDetailsVO reservationDetailsVO = reservationDetailsMapper.selectOneById(no);
        ReservationDetailsDTO reservationDetailsDTO = ReservationDetailsVO.toDTO(reservationDetailsVO);

        // PaymentDetails 들고오기
        PaymentDetailsEntity paymentDetails = paymentDetailsRepository.findById(uuid)
                .orElseThrow(() -> new NoSuchElementException("uuid를 찾을 수 없습니다"));
        PaymentDetailsDTO paymentDetailsDTO = PaymentDetailsEntity.toDTO(paymentDetails);

//        paymentDetailsDTO.getReservation().getSchedule().getMovie().getTitle()

        // 상영관
        Long theaterNo = reservationDetailsDTO.getSchedule().getNo();
        // 인원수
        int personnel = reservationDetailsDTO.getSeatName().size();
        // 좌석 (List<String>)
        List<String> seatName = reservationDetailsDTO.getSeatName();
        // 시작시간
        LocalDateTime startAt1 = reservationDetailsDTO.getSchedule().getStartAt();
        String startAt = startAt1.format(formatter);
        // 종료시간
        LocalDateTime endAt1 = reservationDetailsDTO.getSchedule().getEndAt();
        String endAt = endAt1.format(formatter);
        // 등급
        Rating rating = reservationDetailsDTO.getSchedule().getMovie().getRating();
        // 제목
        String title = reservationDetailsDTO.getSchedule().getMovie().getTitle();
        // 결제 금액
        Long cost = paymentDetailsDTO.getCost();
        // 결제 시간
        LocalDateTime createAt1 = paymentDetailsDTO.getCreateAt();
        String createAt = createAt1.format(formatter);
        // 결제 수단
        Status status = paymentDetailsDTO.getStatus();
//
//        SmsNurigoDTO smsNurigoDTO = SmsNurigoDTO.builder()
//                .no(no1)
//                .title(title)
//                .rating(rating.getConversion())
//                .startAt(startAt)
//                .endAt(endAt)
//                .createAt(createAt)
//                .cost(cost)
//                .seatNumber(seatName)
//                .build();
        String content = "상영관: %s관\n인원수: %d\n좌석: %s\n영화시작시간: %s\n영회종료시간: %s\n등급: %s\n제목: %s\n결제금액: %d원\n결제시간: %s\n예매번호: %s\n결제수단: %s\n"
                .formatted(theaterNo, personnel, seatName, startAt, endAt, rating.getConversion(), title, cost, createAt, no, status);
        log.info("content: {}", content);
        sendSms(toPhone, content);
    }

}
