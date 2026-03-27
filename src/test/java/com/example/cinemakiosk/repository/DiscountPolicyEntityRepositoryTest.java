package com.example.cinemakiosk.repository;

import com.example.cinemakiosk.domain.enums.ConditionType;
import com.example.cinemakiosk.domain.DiscountPolicyEntity;
import com.example.cinemakiosk.domain.enums.DiscountType;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Optional;

@Log4j2
@SpringBootTest
class DiscountPolicyEntityRepositoryTest {
    @Autowired private DiscountPolicyRepository discountPolicyRepository;

    @Test
    void insert() {
        long discountValue = 50;
        DiscountPolicyEntity discountPolicyEntity = DiscountPolicyEntity.builder()
                .policyName("test")
                .discountValue(discountValue)
                .discountType(DiscountType.RATIO)
                .conditionType(ConditionType.AGE)
                .startAt(LocalDateTime.now())
                .endAt(LocalDateTime.now())
                .activation(true)
                .build();

        discountPolicyRepository.save(discountPolicyEntity);
    }

    @Test
    public void select() {
        long id = 1;
        Optional<DiscountPolicyEntity> discountPolicy = discountPolicyRepository.findById(id);
        log.info("discountPolicy={}", discountPolicy);
    }
}