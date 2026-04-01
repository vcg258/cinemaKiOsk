package com.example.cinemakiosk.service;

import com.example.cinemakiosk.domain.MemberEntity;
import com.example.cinemakiosk.domain.PaymentDetailsEntity;
import com.example.cinemakiosk.domain.enums.Status;
import com.example.cinemakiosk.domain.enums.Type;
import com.example.cinemakiosk.dto.MemberDTO;
import com.example.cinemakiosk.dto.PaymentDetailsDTO;
import com.example.cinemakiosk.dto.PointHistoryDTO;
import com.example.cinemakiosk.repository.MemberRepository;
import com.example.cinemakiosk.repository.PaymentDetailsRepository;
import com.example.cinemakiosk.repository.PointHistoryRepository;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.time.LocalDateTime;

@Log4j2
@SpringBootTest
class MemberServiceImplTest {
    @Autowired private MemberService memberService;


    @Test
    void createMember() {
        for (int i = 1; i <= 4; i++) {
            memberService.createMember("0101234523" + i, 20000, "pay-260329-00" + i);
        }
    }


    @Test
    @Rollback(false)
    void pointHistoryCreate() {
        PointHistoryDTO pointHistoryDTO = PointHistoryDTO.builder()
                .paymentId("pay-260329-001")
                .phone("01012345234")
                .type(Type.USE)
                .amountPoint(1000)
                .build();
        memberService.pointHistoryCreate(pointHistoryDTO);
    }

    @Test
    void pointHistoryCancel() {
        PointHistoryDTO pointHistoryDTO = PointHistoryDTO.builder()
                .pointId(8L)
                .paymentId("pay-260329-001")
                .phone("01012345234")
                .type(Type.USE)
                .build();
        memberService.pointHistoryCancel(pointHistoryDTO);
    }

    @Test
    void getMembersAllTest() {
        memberService.getMembersAll().forEach(log::info);
    }

    @Test
    void getMembersTest() {
        log.info(memberService.getMember("01012345234"));
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
                .id("pay-260329-005")
                .cost(10000L)
                .status(Status.PAY)
                .createAt(LocalDateTime.now())
                .build();
        paymentDetailsRepository.save(payment);

        // 2. 회원 더미 데이터
        memberService.createMember("01012345678", 5000, "pay-260329-005");

        // 3. 포인트 내역 더미 데이터
        PointHistoryDTO dto = PointHistoryDTO.builder()
                .paymentId("pay-260329-005")
                .phone("01012345678")
                .type(Type.EARN)
                .amountPoint(5000)
                .createAt(LocalDateTime.now())
                .build();

        memberService.pointHistoryCreate(dto);

        // 4. 테스트
        Long pointId = pointHistoryRepository
                .findByPaymentDetailsEntity_Id("pay-260329-005")
                .getFirst().getPointId();

        PointHistoryDTO pointHistoryDTO = PointHistoryDTO.builder()
                .pointId(pointId)
                .paymentId("pay-260329-005")
                .phone("01012345678")
                .createAt(LocalDateTime.now())
                .build();

        memberService.pointHistoryCancel(pointHistoryDTO);

        // 5. 검증
        MemberEntity result = memberRepository.findById("01012345678").orElseThrow();
        log.info("pointHistoryCancelTest... 환불 후 잔여 포인트: {}", result.getPoint());
    }
}