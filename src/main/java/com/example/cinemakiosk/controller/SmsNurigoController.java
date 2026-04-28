package com.example.cinemakiosk.controller;


import com.example.cinemakiosk.dto.SmsNurigoDTO;
import com.example.cinemakiosk.service.SmsNurigoService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/")
public class SmsNurigoController {
    private final SmsNurigoService smsNurigoService;

    @Operation(summary = "문자발송",
            description = "1. 문자받을 폰 번호 입력\n 2. 내용 입력 (- 유무 상관없음)")
    @PostMapping("/sms")
    public ResponseEntity<Void> sms(@RequestBody SmsNurigoDTO smsNurigoDTO) {
        log.info("Sending SMS to " + smsNurigoDTO.getToPhone());
        log.info("content {} ", smsNurigoDTO.getContent());
        smsNurigoService.sendSms(smsNurigoDTO.getToPhone(), smsNurigoDTO.getContent());
        return ResponseEntity.ok().build();
    }


    @Operation(summary = "인증번호(랜덤6자리숫자)",
            description = "1. 문자받을 폰 번호 입력")
    @PostMapping("/random/{toPhone}")
    public ResponseEntity<Void> random(@PathVariable String toPhone) {
        log.info("Sending random to " + toPhone);
        smsNurigoService.AuthenticationNumber(toPhone);
        return ResponseEntity.ok().build();
    }


}
