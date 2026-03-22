package com.example.cinemakiosk.repository;

import com.example.cinemakiosk.domain.DiscountPolicy.ConditionType;
import com.example.cinemakiosk.domain.DiscountPolicy.DiscountPolicy;
import com.example.cinemakiosk.domain.DiscountPolicy.DiscountType;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Log4j2
@SpringBootTest
class DiscountPolicyRepositoryTest {
    @Autowired private DiscountPolicyRepository discountPolicyRepository;

    @Test
    void insert() {
        long discountValue = 50;
        DiscountPolicy discountPolicy = DiscountPolicy.builder()
                .policyName("test")
                .discountValue(discountValue)
                .discountType(DiscountType.RATIO)
                .conditionType(ConditionType.AGE)
                .startAt(LocalDateTime.now())
                .endAt(LocalDateTime.now())
                .activation(true)
                .build();

        discountPolicyRepository.save(discountPolicy);
    }

    @Test
    public void select() {
        long id = 1;
        Optional<DiscountPolicy> discountPolicy = discountPolicyRepository.findById(id);
        log.info("discountPolicy={}", discountPolicy);
    }
}