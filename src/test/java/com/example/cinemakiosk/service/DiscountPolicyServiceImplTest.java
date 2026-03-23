package com.example.cinemakiosk.service;

import com.example.cinemakiosk.domain.DiscountPolicy.ConditionType;
import com.example.cinemakiosk.domain.DiscountPolicy.DiscountType;
import com.example.cinemakiosk.dto.DiscountPolicyDTO;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

@Log4j2
@SpringBootTest
class DiscountPolicyServiceImplTest {
    @Autowired private DiscountPolicyService discountPolicyService;

    @Test
    public void createCouponNumTest() {
        discountPolicyService.createCouponNum(1L);
    }

    @Test
    public void updateCouponStatusTest() {
        discountPolicyService.updateStatus("55de90a7b78f", true);
    }

    @Test
    public void authCouponTest() {
        boolean result = discountPolicyService.authCoupon(1L, "55de90a7b78f");
        log.info("result: {}", result);
    }

    @Test
    public void finalActivationTest() {
        discountPolicyService.finishActivation(1L);

    }

    @Test
    public void changeActivationTest() {
        discountPolicyService.changeActivation(1L, false);
    }

    @Test
    public void createDiscountPolicyTest() {
        DiscountPolicyDTO discountPolicyDTO = DiscountPolicyDTO.builder()
                .policyName("test1")
                .discountType(DiscountType.RATIO)
                .discountValue(50L)
                .conditionType(ConditionType.JOB)
                .startAt(LocalDateTime.now())
                .endAt(LocalDateTime.now().plusHours(1))
                .activation(true)
                .build();

        discountPolicyService.createDiscountPolicy(discountPolicyDTO);
    }

    @Test
    public void getDiscountPoliciesTest() {
        for (DiscountPolicyDTO discountPolicyDTO : discountPolicyService.getDiscountPolicies()) {
            log.info("All discountPolicyDTO: {}", discountPolicyDTO);
        }
    }

    @Test
    public void getDiscountPolicyTest() {
        log.info("discountPolicyDTO: {}", discountPolicyService.getDiscountPolicy(1L));
    }
}