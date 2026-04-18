package com.example.cinemakiosk.controller;

import com.example.cinemakiosk.dto.BonusPolicyDTO;
import com.example.cinemakiosk.dto.CouponDTO;
import com.example.cinemakiosk.dto.DiscountPolicyDTO;
import com.example.cinemakiosk.dto.RequestDTO.ActivationRequest;
import com.example.cinemakiosk.service.DiscountPolicyService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j2
@RestController
@RequestMapping("/api/admin/discount-policy")
@RequiredArgsConstructor
public class DiscountPolicyController {
    private final DiscountPolicyService discountPolicyService;

    @Operation(summary = "할인정책 등록")
    @PostMapping
    public ResponseEntity<Void> addDiscountPolicies(@RequestBody DiscountPolicyDTO discountPolicyDTO) {
        discountPolicyService.createDiscountPolicy(discountPolicyDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build(); // 생성
    }

    @Operation(summary = "할인정책 전체 조회")
    @GetMapping("/list")
    public ResponseEntity<List<DiscountPolicyDTO>> getDiscountPolicies() {
        return ResponseEntity.ok(discountPolicyService.getDiscountPolicies());
    }

    @Operation(summary = "할인정책 단일 조회")
    @GetMapping("/{id}")
    public ResponseEntity<DiscountPolicyDTO> getDiscountPolicy(@PathVariable Long id) {
        return ResponseEntity.ok(discountPolicyService.getDiscountPolicy(id));
    }

    @Operation(summary = "할인정책 종료지정")
    @PatchMapping("/{id}/finish")
    public ResponseEntity<Void> finishDiscountPolicy(@PathVariable Long id) {
        discountPolicyService.finishActivation(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "할인정책 활성화 비활성화")
    @PatchMapping("/activation")
    public ResponseEntity<Void> changeDiscountPolicyActivation(@RequestBody ActivationRequest request) {
        discountPolicyService.changeActivation(request);
        return ResponseEntity.noContent().build(); // 204 변경됨 하지만 다시 값을 보여줄 필요는 없음
    }

    @Operation(summary = "지정 정책에 쿠폰 발행")
    @PostMapping("/coupon/{policyId}")
    public ResponseEntity<Void> addCoupon(@PathVariable Long policyId, @RequestParam(defaultValue = "1") int count) { // TODO 따로 DTO를 넣자는 의견이 있음 일단 보류
        discountPolicyService.createCouponNum(policyId, count);
        return ResponseEntity.status(HttpStatus.CREATED).build(); // 201 생성
    }

    @Operation(summary = "쿠폰 전체 조회 (폐이징 처리)")
    @GetMapping("/coupon/list")
    public ResponseEntity<Page<CouponDTO>> getCoupons(@RequestParam Integer page) {
        return ResponseEntity.ok(discountPolicyService.getCouponAll(page));
    }

    @Operation(summary = "지정 쿠폰 조회")
    @GetMapping("/coupon/{couponNum}")
    public ResponseEntity<CouponDTO> getCoupon(@PathVariable String couponNum) {
        return ResponseEntity.ok(discountPolicyService.getCoupon(couponNum));
    }

    @Operation(summary = "할인정책 (페이징 처리 size=10 고정)")
    @GetMapping("/log")
    public ResponseEntity<Page<DiscountPolicyDTO>> getBonusPolicies(@RequestParam(defaultValue = "1") int page) {
        return ResponseEntity.ok(discountPolicyService.getDiscountPolicyPage(page));
    }
}
