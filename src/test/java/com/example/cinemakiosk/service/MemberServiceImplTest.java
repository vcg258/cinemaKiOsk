package com.example.cinemakiosk.service;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@Log4j2
@SpringBootTest
class MemberServiceImplTest {
    @Autowired private MemberService memberService;

    @Test
    void createMember() {
        memberService.createMember("01012341234", 5200);
    }

//    @Test
//    void remainingPoint() {
//        memberService.remainingPoint("01013341234", 500);
//    }

    @Test
    void pointHistoryCreate() {
    }

    @Test
    void pointHistoryCancel() {
    }
}