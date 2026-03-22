package com.example.cinemakiosk.service;

import com.example.cinemakiosk.domain.Coupon;
import com.example.cinemakiosk.domain.DiscountPolicy.DiscountPolicy;
import com.example.cinemakiosk.dto.CouponDTO;
import com.example.cinemakiosk.mapper.CouponMapper;
import com.example.cinemakiosk.mapper.DiscountPolicyMapper;
import com.example.cinemakiosk.repository.CouponRepository;
import com.example.cinemakiosk.repository.DiscountPolicyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Log4j2
@Service
@RequiredArgsConstructor
public class DiscountPolicyServiceImpl implements DiscountPolicyService {
    private final DiscountPolicyMapper discountPolicyMapper;
    private final DiscountPolicyRepository discountPolicyRepository;
    private final CouponMapper couponMapper;
    private final CouponRepository couponRepository;


    /**
     * 특정 정책의 쿠폰 번호 발행
     * @param policyId 정책 번호 FK
     */
    @Override
    public void createCouponNum(Long policyId) {
        String couponNum = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 12);
        CouponDTO couponDTO = CouponDTO.builder()
                .couponNum(couponNum)
                .policyId(policyId)
                .status(true) // 사용 가능
                .build();

        log.info("couponDTO: {}", couponDTO);
        DiscountPolicy discountPolicy = discountPolicyRepository.getReferenceById(policyId);
        Coupon coupon = CouponDTO.fromDTO(couponDTO, discountPolicy);
        couponRepository.save(coupon);
    }

    @Override
    public boolean authCoupon(Long policyId) {
        return false;
    }

    @Override
    public void updateStatus(Long policyId) {

    }

    @Override
    public void finishActivation(Long id) {

    }
}
