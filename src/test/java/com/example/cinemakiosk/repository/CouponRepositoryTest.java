package com.example.cinemakiosk.repository;

import com.example.cinemakiosk.domain.Coupon;
import com.example.cinemakiosk.domain.DiscountPolicy.DiscountPolicy;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Log4j2
@SpringBootTest
class CouponRepositoryTest {
    @Autowired private CouponRepository couponRepository;
    @Autowired private DiscountPolicyRepository discountPolicyRepository;

    // 쿠폰 더미 데이터 테스트
    @Test
    void insertDummy() {
        for (int i = 0; i < 5; i++) {
            String couponNum = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 12);

            Coupon coupon = Coupon.builder()
                    .couponNum(couponNum)
                    .status(true)
                    .discountPolicy(discountPolicyRepository.getReferenceById(1L)) // 쿼리문 진입하지 않고 그냥 id값만 필요할때
                    .build();

            couponRepository.save(coupon);
        }
    }

    @Test
    void select() {
        couponRepository.findAll().forEach(log::info);
    }
}