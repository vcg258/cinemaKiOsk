package com.example.cinemakiosk.service;

import com.example.cinemakiosk.domain.Coupon;
import com.example.cinemakiosk.domain.DiscountPolicy.DiscountPolicy;
import com.example.cinemakiosk.dto.CouponDTO;
import com.example.cinemakiosk.dto.DiscountPolicyDTO;
import com.example.cinemakiosk.mapper.CouponMapper;
import com.example.cinemakiosk.mapper.DiscountPolicyMapper;
import com.example.cinemakiosk.repository.CouponRepository;
import com.example.cinemakiosk.repository.DiscountPolicyRepository;
import com.example.cinemakiosk.vo.CouponVO;
import com.example.cinemakiosk.vo.DiscountPolicyVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
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

    /**
     * 쿠폰 사용 검증 (정책 기간, 사용여부, 정책에 해당하는 쿠폰, 쿠폰번호)
     * @param policyId 정책 번호 FK
     * @param couponNum 쿠폰 번호 검사
     * @return 사용 검증 통과면 true, 아니면 false
     */
    @Override
    public boolean authCoupon(Long policyId, String couponNum) {
        DiscountPolicyDTO discountPolicyDTO = discountPolicyMapper.checkCoupon(policyId);
        // 정책이 없을 경우
        if (discountPolicyDTO == null) return false;
        // 정책이 비활성화 일 경우
        if (!discountPolicyDTO.isActivation()) return false;
        // 할인 정책이 만료된 경우
        LocalDateTime now = LocalDateTime.now();
        if (!(now.isBefore(discountPolicyDTO.getEndAt()) && now.isAfter(discountPolicyDTO.getStartAt()))) return false;
        // 쿠폰 번호가 일치하고 사용여부가 true일경우
        for (CouponVO couponVO : discountPolicyDTO.getCoupons()) {
            if (couponVO.getCouponNum().equals(couponNum)) {
                return couponVO.isStatus();
            }
        }

        return false; // 나머지 불가능
    }

    /**
     * 쿠폰을 사용함으로써 사용여부 업데이트
     * @param couponNum 쿠폰번호
     */
    @Override
    public void updateStatus(String couponNum) {
        Coupon coupon = couponRepository.findById(couponNum).orElseThrow();
        coupon.changeStatus(false); // 사용후 변경
        couponRepository.save(coupon);
        log.info("coupon: {}", coupon);
    }

    /**
     * 할인정책 종료 (23시 59분으로 지정 활성화 여부 FALSE)
     * @param id 정책 번호 FK
     */
    @Override
    public void finishActivation(Long id) {

    }
}
