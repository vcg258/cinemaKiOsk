package com.example.cinemakiosk.service.Ai;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@Log4j2
@SpringBootTest
class RagServiceTest {
    @Autowired RagService ragService;

    @Test
    void chat() {
        ragService.chat("그럼 국회의원은?", 0.3, "", "admin12");
    }

    @Test
    void debugScore() {
        ragService.debugScore("대통령의 임기는?", "헌법");
    }
}