package com.example.cinemakiosk.service;

import com.example.cinemakiosk.domain.CouponEntity;
import com.example.cinemakiosk.domain.DiscountPolicyEntity;
import com.example.cinemakiosk.dto.CouponDTO;
import com.example.cinemakiosk.dto.DiscountPolicyDTO;
import com.example.cinemakiosk.mapper.DiscountPolicyMapper;
import com.example.cinemakiosk.repository.CouponRepository;
import com.example.cinemakiosk.repository.DiscountPolicyRepository;
import com.example.cinemakiosk.vo.CouponVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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

        DiscountPolicyEntity dto1 = DiscountPolicyDTO.toEntity(dto);
        log.info(dto1);
        discountPolicyRepository.save(dto1);
    }

    /**
     * 할인 정책 전체 조회
     * @return 할인정책 전채 리스트
     */
    @Override
    public List<DiscountPolicyDTO> getDiscountPolicies() {
        List<DiscountPolicyEntity> discountPolicies = discountPolicyRepository.findAll();
        List<DiscountPolicyDTO> discountPolicyDTO = new ArrayList<>();
        for (DiscountPolicyEntity discountPolicyEntity : discountPolicies) {
            DiscountPolicyDTO dto = DiscountPolicyEntity.toDTO(discountPolicyEntity);
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
        DiscountPolicyEntity discountPolicyEntity = discountPolicyRepository.findById(id).orElseThrow();
        return DiscountPolicyEntity.toDTO(discountPolicyEntity);
    }

    /**
     * 할인정책 종료 (23시 59분으로 지정 활성화 여부 FALSE)
     * @param id 정책 번호 FK
     */
    @Override
    public void finishActivation(Long id) { // TODO batch 사용으로 만료시간이 되면 자동 비활성화로 변경 해야함
        DiscountPolicyEntity discountPolicyEntity = discountPolicyRepository.findById(id).orElseThrow();
        if (LocalDateTime.now().isAfter(discountPolicyEntity.getEndAt()) || !discountPolicyEntity.isActivation()) {
            log.error("finishActivation... 이미 비활성화 된 정책입니다. {}", discountPolicyEntity);
            return;
        }

        discountPolicyEntity.finalDiscountPolicy(LocalDateTime.now().withHour(23).withMinute(59).withSecond(59));
        DiscountPolicyEntity policy = discountPolicyRepository.save(discountPolicyEntity);
        log.info("finishActivation... 할인정책 종료시간 지정 : {}", policy);
    }

    /**
     * 할인 정책 활성화 / 비활성화
     * @param id 정책 번호 FK
     * @param activation 할인 정책 상태
     */
    @Override
    public void changeActivation(Long id, boolean activation) {
        DiscountPolicyEntity discountPolicyEntity = discountPolicyRepository.findById(id).orElseThrow();
        discountPolicyEntity.changeActivation(activation);
        discountPolicyRepository.save(discountPolicyEntity);
        log.info("changeActivation discountPolicy: {}", discountPolicyEntity);
    }

    /**
     * 특정 정책의 쿠폰 번호 발행
     * @param policyId 정책 번호 FK
     */
    @Override
    public void createCouponNum(Long policyId) {
        String couponNum = UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        CouponDTO couponDTO = CouponDTO.builder()
                .couponNum(couponNum)
                .policyId(policyId)
                .status(true) // 사용 가능
                .build();

        log.info("couponDTO: {}", couponDTO);
        CouponEntity couponEntity = CouponDTO.toEntity(couponDTO);
        couponRepository.save(couponEntity);
    }

    /**
     * 쿠폰 사용 검증 (정책 기간, 사용여부, 정책에 해당하는 쿠폰, 쿠폰번호)
     * @param policyId 정책 번호 FK
     * @param couponNum 쿠폰 번호 검사
     * @return 사용 검증 통과면 true, 아니면 false
     */
    @Override
    public boolean authCoupon(Long policyId, String couponNum) { // TODO for로 돌리는것보다 DB로 처리하는게 훨씬 효율적 FIX요청
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
        CouponEntity couponEntity = couponRepository.findById(couponNum).orElseThrow();
        couponEntity.changeStatus(status); // 사용후 변경
        couponRepository.save(couponEntity);
        log.info("coupon: {}", couponEntity);
    }

    /**
     * 10페이지씩 페이징 처리 (로그 포함 전체 조회)
     * @param page 몇번째 페이지 부터 가져올건지 정하는 변수
     * @return 페이징 결과 1페이지 일경우 1 ~ 10번 까지
     */
    @Override
    public Page<DiscountPolicyDTO> getDiscountPolicyPage(int page) {
        Pageable pageable = PageRequest.of(page - 1, 10, Sort.by("id").descending());
        Page<DiscountPolicyEntity> policy = discountPolicyRepository.findAll(pageable);
        return policy.map(discountPolicy -> DiscountPolicyEntity.toDTO(discountPolicy));
    }

    /**
     * 쿠폰 전체 조회
     * @return 전체 쿠폰을 담은 리스트
     */
    @Override
    public List<CouponDTO> getCouponAll() {
        List<CouponEntity> entityList = couponRepository.findAll();
        return entityList.stream().map(CouponEntity::toDTO).toList();
    }

    /**
     * 쿠폰 단일 조회
     * @param couponNum 쿠폰 번호
     * @return 쿠폰 하나 조회
     */
    @Override
    public CouponDTO getCoupon(String couponNum) {
        return CouponEntity.toDTO(couponRepository.findById(couponNum).orElseThrow());
    }
}
