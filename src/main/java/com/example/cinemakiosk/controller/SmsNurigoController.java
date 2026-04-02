package com.example.cinemakiosk.controller;


import com.example.cinemakiosk.service.SmsNurigoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequiredArgsConstructor
public class SmsNurigoController {
    private final SmsNurigoService smsNurigoService;

    @PostMapping("/api/sms")
    public void Sms(String toPhone, String content) {
        log.info("Sending SMS to " + toPhone);
        log.info("content {} ", content);
        smsNurigoService.sendSms(toPhone, content);

    }
}
