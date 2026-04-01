package com.example.cinemakiosk.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Log4j2
@RequestMapping("/test-websocket")
public class TestWebsocketController {
    @GetMapping("/first")
    public void getFirst(){}
    @GetMapping("/second")
    public void getSecond(){}
    @GetMapping("/payment")
    public void getPayment(@RequestParam("scheduleId") Long scheduleId){
        log.info("scheduleId 수신 : {} ", scheduleId);
    }
}
