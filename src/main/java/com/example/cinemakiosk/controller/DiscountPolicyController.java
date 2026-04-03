package com.example.cinemakiosk.controller;

import com.example.cinemakiosk.dto.DiscountPolicyDTO;
import com.example.cinemakiosk.service.DiscountPolicyService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping("/api/admin/discount-policy")
@RequiredArgsConstructor
public class DiscountPolicyController {
    private final DiscountPolicyService discountPolicyService;

    @Operation(summary = "할인정책 등록")
    @PostMapping("")
    public ResponseEntity<Void> addDiscountPolicies(@RequestBody DiscountPolicyDTO discountPolicyDTO) {
        discountPolicyService.createDiscountPolicy(discountPolicyDTO);
        return ResponseEntity.ok().build();
    }
}
