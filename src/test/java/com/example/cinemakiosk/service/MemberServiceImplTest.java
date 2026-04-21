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
            MemberDTO memberDTO = MemberDTO.builder()
                    .phone("0101234523" + i)
                    .point(20000 + i)
                    .build();
            memberService.createMember(memberDTO);
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
        memberService.getMembersAll(1).forEach(log::info);
    }

    @Test
    void getMembersAllLogTest() {
        memberService.getMembersAllLog("01088771113").forEach(log::info);
    }

    @Test
    void getMemberTest() {
        log.info(memberService.getMember("01012345234"));
    }

    @Test
    void getPointHistory() {
        memberService.getPointHistoryAll(1).forEach(log::info);
    }
}