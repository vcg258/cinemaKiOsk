package com.example.cinemakiosk.service;

import com.example.cinemakiosk.dto.PaymentDetailsDTO;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Log4j2
@SpringBootTest
class PaymentDetailsServiceImplTest {
    @Autowired private PaymentDetailsService paymentDetailsService;

    @Test
    void selectAll() {
        paymentDetailsService.readAll(1).forEach(log::info);
    }

    @Test
    void updateToReturn() {
        PaymentDetailsDTO read = paymentDetailsService.read("33d0138a-b948-41e7-8c97-3dc043b18fa0");
        paymentDetailsService.updateToReturn(read);
    }
}