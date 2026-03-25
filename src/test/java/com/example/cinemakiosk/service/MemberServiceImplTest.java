package com.example.cinemakiosk.service;

import com.example.cinemakiosk.domain.PaymentDetailsEntity.PaymentDetailsEntity;
import com.example.cinemakiosk.domain.PaymentDetailsEntity.Status;
import com.example.cinemakiosk.domain.PointHistoryEntity.Type;
import com.example.cinemakiosk.repository.PaymentDetailsRepository;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.Rollback;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@Log4j2
@SpringBootTest
class MemberServiceImplTest {
    @Autowired private MemberService memberService;
    @Autowired
    private PaymentDetailsRepository paymentDetailsRepository;

    @Test
    void createMember() {
        memberService.createMember("01012345234", 5200, "PAY_002");
    }


    @Test
    @Rollback(false)
    void pointHistoryCreate() {
        memberService.pointHistoryCreate("01012341234", 1000, Type.EARN, "PAY_001");
    }

    @Test
    void pointHistoryCancel() {
    }

    @Test
    void dummyPayment() {
        PaymentDetailsEntity payment = PaymentDetailsEntity.builder()
                .id("PAY_001")
                .cost(20000L)
                .status(Status.PAY)
                .time(LocalDateTime.now())
                .usePoint(0L)
                .build();
        paymentDetailsRepository.save(payment);
    }
}