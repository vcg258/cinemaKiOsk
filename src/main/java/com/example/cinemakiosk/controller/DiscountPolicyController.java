package com.example.cinemakiosk.controller;

import com.example.cinemakiosk.dto.DiscountPolicyDTO;
import com.example.cinemakiosk.service.DiscountPolicyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping("/api/admin/discount-policy")
@RequiredArgsConstructor
public class DiscountPolicyController {
    private final DiscountPolicyService discountPolicyService;

    @PostMapping("")
    public void getDiscountPolicies(DiscountPolicyDTO discountPolicyDTO) {
    }
}
