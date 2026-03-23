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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Log4j2
@Service
@RequiredArgsConstructor
public class DiscountPolicyServiceImpl implements DiscountPolicyService {
    private final DiscountPolicyMapper discountPolicyMapper;
    private final DiscountPolicyRepository discountPolicyRepository;
    private final CouponRepository couponRepository;

    /**
     * 할인 정책 추가 / 수정
     * @param discountPolicyDTO 할인 정책 DTO
     */
    @Override
    public void createDiscountPolicy(DiscountPolicyDTO discountPolicyDTO) {
        // 현재 활성화 된 할인정책 이름 중복 방지
        if (discountPolicyRepository.existsByPolicyNameAndEndAtAfter(discountPolicyDTO.getPolicyName(), LocalDateTime.now())) {
            throw new IllegalArgumentException("이미 사용하는 정책 이름입니다.");
        }
        DiscountPolicyDTO dto = DiscountPolicyDTO.builder()
                .id(discountPolicyDTO.getId())
                .policyName(discountPolicyDTO.getPolicyName())
                .discountType(discountPolicyDTO.getDiscountType())
                .discountValue(discountPolicyDTO.getDiscountValue())
                .conditionType(discountPolicyDTO.getConditionType())
                .startAt(discountPolicyDTO.getStartAt())
                .endAt(discountPolicyDTO.getEndAt())
                .activation(discountPolicyDTO.isActivation())
                .build();

        DiscountPolicy dto1 = DiscountPolicyDTO.fromDTO(dto);
        log.info(dto1);
        discountPolicyRepository.save(dto1);
    }

    /**
     * 할인 정책 전체 조회
     * @return 할인정책 전채 리스트
     */
    @Override
    public List<DiscountPolicyDTO> getDiscountPolicies() {
        List<DiscountPolicy> discountPolicies = discountPolicyRepository.findAll();
        List<DiscountPolicyDTO> discountPolicyDTO = new ArrayList<>();
        for (DiscountPolicy discountPolicy : discountPolicies) {
            DiscountPolicyDTO dto = DiscountPolicyDTO.toDTO(discountPolicy);
            discountPolicyDTO.add(dto);
        }
        return discountPolicyDTO;
    }

    /**
     * 할인 정책 단일 조회
     * @param id 정책 아이디
     * @return 할인 정책 단일
     */
    @Override
    public DiscountPolicyDTO getDiscountPolicy(Long id) {
        DiscountPolicy discountPolicy = discountPolicyRepository.findById(id).orElseThrow();
        return DiscountPolicyDTO.toDTO(discountPolicy);
    }

    /**
     * 할인정책 종료 (23시 59분으로 지정 활성화 여부 FALSE)
     * @param id 정책 번호 FK
     */
    @Override
    public void finishActivation(Long id) {
        DiscountPolicy discountPolicy = discountPolicyRepository.findById(id).orElseThrow();
        discountPolicy.finalDiscountPolicy(LocalDateTime.now().withHour(23).withMinute(59).withSecond(59));
        log.info("Finish discountPolicy: {}", discountPolicy);
        discountPolicyRepository.save(discountPolicy);
    }

    /**
     * 할인 정책 활성화 / 비활성화
     * @param id 정책 번호 FK
     * @param activation 할인 정책 상태
     */
    @Override
    public void changeActivation(Long id, boolean activation) {
        DiscountPolicy discountPolicy = discountPolicyRepository.findById(id).orElseThrow();
        discountPolicy.changeActivation(activation);
        log.info("changeActivation discountPolicy: {}", discountPolicy);
    }

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
        // 정책이 없을 경우 (INNER JOIN을 하였기때문에 정책이 없다면 null)
        if (discountPolicyDTO == null) return false;
        // 정책이 비활성화 일 경우
        if (!discountPolicyDTO.isActivation()) return false;
        // 할인 정책이 만료된 경우
        LocalDateTime now = LocalDateTime.now();
        if (!(now.isBefore(discountPolicyDTO.getEndAt()) && now.isAfter(discountPolicyDTO.getStartAt()))) return false;
        // 쿠폰 번호가 일치하고 사용여부가 true일경우
        for (CouponVO couponVO : discountPolicyDTO.getCoupons()) {
            if (couponVO.getCouponNum().equals(couponNum)) {
                return couponVO.isStatus(); // true 사용가능
            }
        }

        return false; // 나머지 불가능
    }

    /**
     * 쿠폰을 사용함으로써 사용여부 업데이트 및 환불시 쿠폰 복구
     * @param couponNum 쿠폰번호
     */
    @Override
    public void updateStatus(String couponNum, boolean status) {
        Coupon coupon = couponRepository.findById(couponNum).orElseThrow();
        coupon.changeStatus(status); // 사용후 변경
        couponRepository.save(coupon);
        log.info("coupon: {}", coupon);
    }
}
