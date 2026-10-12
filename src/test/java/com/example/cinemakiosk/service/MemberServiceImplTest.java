package com.example.cinemakiosk.service;

import com.example.cinemakiosk.domain.enums.Type;
import com.example.cinemakiosk.dto.PointHistoryDTO;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;


@Log4j2
@SpringBootTest
class MemberServiceImplTest {
    @Autowired private MemberService memberService;

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
        memberService.getMembersAllLog("01099999999").forEach(log::info);
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