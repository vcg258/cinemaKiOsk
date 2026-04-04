package com.example.cinemakiosk.controller;

import com.example.cinemakiosk.dto.DiscountPolicyDTO;
import com.example.cinemakiosk.service.DiscountPolicyService;
import com.solapi.shadow.retrofit2.http.Path;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    @Operation(summary = "할인정책 종료지정", description = "만료시간이 지났거나 비활성화 일 경우 그냥 넘어감")
    @PatchMapping("/{id}/finish")
    public ResponseEntity<Void> finishDiscountPolicy(@PathVariable Long id) {
        discountPolicyService.finishActivation(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "할인정책 활성화 비활성화",
            description = "ex) {\"ids\": [1, 2], \"activation\": false}")
    @PatchMapping("/activation")
    public ResponseEntity<Void> changeDiscountPolicyActivation(@RequestBody Map<String, Object> request) {
        List<Long> ids = (List<Long>) request.get("ids");
        boolean activation = (boolean) request.get("activation");
        discountPolicyService.changeActivation(ids, activation);
        return ResponseEntity.ok().build();
    }
}
