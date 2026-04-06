package com.example.cinemakiosk.controller;


import com.example.cinemakiosk.dto.SmsNurigoDTO;
import com.example.cinemakiosk.service.SmsNurigoService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequiredArgsConstructor
public class SmsNurigoController {
    private final SmsNurigoService smsNurigoService;

    @Operation(summary = "문자발송",
            description = "1. 문자받을 폰 번호 입력\n 2. 내용 입력 (- 유무 상관없음)")
    @PostMapping("/api/sms")
    public ResponseEntity<Void> Sms(@RequestBody SmsNurigoDTO smsNurigoDTO) {
        log.info("Sending SMS to " + smsNurigoDTO.getToPhone());
        log.info("content {} ", smsNurigoDTO.getContent());
        smsNurigoService.sendSms(smsNurigoDTO.getToPhone(), smsNurigoDTO.getContent());
        return ResponseEntity.ok().build();

    }
}
