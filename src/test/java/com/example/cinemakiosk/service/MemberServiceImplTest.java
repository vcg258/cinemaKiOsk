package com.example.cinemakiosk.service;

import com.example.cinemakiosk.domain.MemberEntity;
import com.example.cinemakiosk.domain.PaymentDetailsEntity.PaymentDetailsEntity;
import com.example.cinemakiosk.domain.PaymentDetailsEntity.Status;
import com.example.cinemakiosk.domain.PointHistoryEntity.PointHistoryEntity;
import com.example.cinemakiosk.domain.PointHistoryEntity.Type;
import com.example.cinemakiosk.dto.PointHistoryDTO;
import com.example.cinemakiosk.repository.MemberRepository;
import com.example.cinemakiosk.repository.PaymentDetailsRepository;
import com.example.cinemakiosk.repository.PointHistoryRepository;
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


    @Test
    void createMember() {
        for (int i = 1; i <= 5; i++) {
            memberService.createMember("0101234523" + i, 20000, "PAY_00" + i);
        }
    }


    @Test
    @Rollback(false)
    void pointHistoryCreate() {
        PointHistoryDTO pointHistoryDTO = PointHistoryDTO.builder()
                .paymentId("TEST-PAYMENT-UUID-001")
                .phone("01012345678")
                .type(Type.USE)
                .amountPoint(1000)
                .build();
        memberService.pointHistoryCreate(pointHistoryDTO);
    }

    @Test
    void pointHistoryCancel() {
        PointHistoryDTO pointHistoryDTO = PointHistoryDTO.builder()
                .build();
        memberService.pointHistoryCancel(pointHistoryDTO);
    }





    // TODO 클로드 테스트 코드 부탁한 것 기능 해보다가 다 된다면 지우면 될듯
    @Autowired private PaymentDetailsRepository paymentDetailsRepository;
    @Autowired private MemberRepository memberRepository;
    @Autowired private PointHistoryRepository pointHistoryRepository;
    @Test
    @Transactional
    @Rollback
    public void pointHistoryCancelTest() {
        // 1. 결제 더미 데이터
        PaymentDetailsEntity payment = PaymentDetailsEntity.builder()
                .id("TEST-PAYMENT-UUID-001")
                .cost(10000L)
                .createAt(LocalDateTime.now())
                .status(Status.PAY)
                .build();
        paymentDetailsRepository.save(payment);

        // 2. 회원 더미 데이터
        memberService.createMember("01012345678", 5000, "TEST-PAYMENT-UUID-001");

        // 3. 포인트 내역 더미 데이터
        PointHistoryDTO dto = PointHistoryDTO.builder()
                .paymentId("TEST-PAYMENT-UUID-001")
                .phone("01012345678")
                .type(Type.EARN)
                .amountPoint(5000)
                .build();

        memberService.pointHistoryCreate(dto);

        // 4. 테스트
        Long pointId = pointHistoryRepository
                .findByPaymentDetailsEntity_Id("TEST-PAYMENT-UUID-001")
                .getFirst().getPointId();

        PointHistoryDTO pointHistoryDTO = PointHistoryDTO.builder()
                .pointId(pointId)
                .paymentId("TEST-PAYMENT-UUID-001")
                .phone("01012345678")
                .build();

        memberService.pointHistoryCancel(pointHistoryDTO);

        // 5. 검증
        MemberEntity result = memberRepository.findById("01012345678").orElseThrow();
        log.info("pointHistoryCancelTest... 환불 후 잔여 포인트: {}", result.getPoint());
    }
}