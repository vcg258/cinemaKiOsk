package com.example.cinemakiosk.service;

import com.example.cinemakiosk.dto.BonusPolicyDTO;
import com.example.cinemakiosk.dto.RequestDTO.ActivationRequest;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

@Log4j2
@SpringBootTest
class BonusPolicyServiceImplTest {
    @Autowired
    BonusPolicyService bonusService;

//    @Test
//    void createBonusPolicy() {
//        BonusPolicyDTO dto = BonusPolicyDTO.builder()
//                .policyName("test")
//                .giveValue(5000L)
//                .startAt(LocalDateTime.now())
//                .finishedAt(LocalDateTime.now().plusDays(3))
//                .activation(true)
//                .build();
//        bonusService.createBonusPolicy(dto);
//    }

    @Test
    void finishActivation() {
        bonusService.finishActivation(5L);
    }

    @Test
    void changeActivation() {
        ActivationRequest request = new ActivationRequest();
        request.setIds(List.of(1L, 2L));
        request.setActivation(true);
        bonusService.changeActivation(request);
    }

    @Test
    void getBonusPolicies() {
        bonusService.getBonusPolicies().forEach(log::info);
    }

    @Test
    void getBonusPolicy() {
        log.info(bonusService.getBonusPolicy(5L));
    }

    @Test
    void getBonusPolicyPage() {
        Page<BonusPolicyDTO> page = bonusService.getBonusPolicyPage(1);
        log.info("전체 개수 {}", page.getTotalElements());
        log.info("전체 페이지 {}", page.getTotalPages());
        log.info("전체 내용 {}", page.getContent());
    }
}