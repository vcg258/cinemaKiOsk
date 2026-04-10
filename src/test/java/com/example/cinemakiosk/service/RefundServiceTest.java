package com.example.cinemakiosk.service;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@Log4j2
@SpringBootTest
class RefundServiceTest {
    @Autowired RefundService refundService;

    @Test
    void refund() {
        String paymentId = "33333333-3333-3333-3333-333333333333";
        refundService.refund(paymentId);
    }
}