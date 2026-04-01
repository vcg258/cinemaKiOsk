package com.example.cinemakiosk.service;

import com.example.cinemakiosk.dto.BonusPolicyDTO;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Log4j2
@SpringBootTest
class BonusPolicyServiceImplTest {

    @Autowired
    private BonusPolicyService bonusPolicyService;


    // 등록
    @Test
    void addBonusPolicy() {
        BonusPolicyDTO bonusPolicyDTO = BonusPolicyDTO.builder()
                .policyName("테스트 정책")
                .giveValue(30L)
                .activation(true)
                .createAt(LocalDateTime.now())
                .finishedAt(LocalDateTime.now())
                .build();

        bonusPolicyService.addBonusPolicy(bonusPolicyDTO);

    }

    // 조회
    @Test
    void getBonusPolicies() {
        List<BonusPolicyDTO> bonusPolicyDTOList = bonusPolicyService.getBonusPolicies();
        for (BonusPolicyDTO bonusPolicyDTO : bonusPolicyDTOList) {
            log.info(bonusPolicyDTO);
        }
    }
}