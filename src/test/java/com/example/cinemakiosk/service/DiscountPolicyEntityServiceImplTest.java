package com.example.cinemakiosk.service;

import com.example.cinemakiosk.domain.enums.ConditionType;
import com.example.cinemakiosk.domain.enums.DiscountType;
import com.example.cinemakiosk.dto.DiscountPolicyDTO;
import com.example.cinemakiosk.repository.CouponRepository;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

@Log4j2
@SpringBootTest
class DiscountPolicyEntityServiceImplTest {
    @Autowired private DiscountPolicyService discountPolicyService;

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

    @Test
    public void finalActivationTest() {
        discountPolicyService.finishActivation(1L);

    }

    @Test
    public void changeActivationTest() {
        discountPolicyService.changeActivation(1L, true);
    }

    @Test
    public void createCouponNumTest() {
        discountPolicyService.createCouponNum(1L);
    }

    @Test
    public void authCouponTest() {
        boolean result = discountPolicyService.authCoupon(1L, "8930bc2b36dd");
        log.info("result: {}", result);
    }

    @Test
    public void updateCouponStatusTest() {
        discountPolicyService.updateStatus("8930bc2b36dd", false);
    }

    @Test
    public void getDiscountPolicyPageTest() {
        Page<DiscountPolicyDTO> page = discountPolicyService.getDiscountPolicyPage(1);
        log.info("전체 개수: {}", page.getTotalElements());
        log.info("전체 페이지: {}", page.getTotalPages());
        log.info("전체 페이지 내용: {}", page.getContent());
    }

    @Test
    public void getCouponAllTest() {
        discountPolicyService.getCouponAll().forEach(log::info);
    }

    @Test
    public void getCouponTest() {
        log.info(discountPolicyService.getCoupon("8930bc2b36dd"));
    }
}