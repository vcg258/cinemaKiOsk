package com.example.cinemakiosk.controller;


import com.example.cinemakiosk.service.SmsNurigoService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sms")
public class SmsNurigoController {
    private final SmsNurigoService smsNurigoService;

    @Operation(summary = "문자발송",
            description = "1. 문자받을 폰 번호 입력\n 2. 내용 입력 (- 유무 상관없음)")
    @PostMapping("/{toPhone}/{inputCode}")
    public ResponseEntity<Void> sms(@PathVariable String toPhone, @PathVariable String inputCode) {
        smsNurigoService.sendSms(toPhone, inputCode);
        return ResponseEntity.ok().build();
    }


    @Operation(summary = "인증번호(랜덤6자리숫자)",
            description = "1. 문자받을 폰 번호 입력")
    @PostMapping("/random/{toPhone}")
    public ResponseEntity<Void> random(@PathVariable String toPhone) {
        log.info("Sending random to {}", toPhone);
        smsNurigoService.AuthenticationNumber(toPhone);
        return ResponseEntity.ok().build();
    }


    @Operation(summary = "인증번호 검증",
            description = "- 대상 고객의 번호와 고객이 입력한 인증번호 입력\n -요청을 받을 때 인증번호가 만료(3분)되었거나 인증 성공시에 인증번호삭제")
    @PostMapping("/comparison/{toPhone}/{inputCode}")
    public ResponseEntity<String> comparison(@PathVariable String toPhone, @PathVariable String inputCode) {
        log.info("comparison to {}", inputCode);

        return switch (smsNurigoService.comparison(toPhone, inputCode)) {
            case SUCCESS -> ResponseEntity.ok().body("인증 성공됐습니다.");
            case INVALID_FORMAT -> ResponseEntity.badRequest().body("인증번호는 6자리입니다.");
            case EXPIRED -> ResponseEntity.badRequest().body("인증번호가 만료됐습니다.");
            case MISMATCH -> ResponseEntity.badRequest().body("인증번호가 일치하지 않습니다.");
            case None -> ResponseEntity.badRequest().body("발송된 인증번호가 없습니다. 인증번호를 다시 발송받으세요.");
        };
    }


    @Operation(summary = "영수증")
    @PostMapping("/receipt/{toPhone}/{uuid}")
    public ResponseEntity<Void> receipt(@PathVariable String toPhone, @PathVariable String uuid) {
        smsNurigoService.receipt(toPhone, uuid);
        return ResponseEntity.ok().build();
    }


    }
