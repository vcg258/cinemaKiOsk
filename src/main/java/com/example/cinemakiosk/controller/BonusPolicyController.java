package com.example.cinemakiosk.controller;

import com.example.cinemakiosk.dto.BonusPolicyDTO;
import com.example.cinemakiosk.dto.RequestDTO.ActivationRequest;
import com.example.cinemakiosk.service.BonusPolicyService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/bonus-policy")
@RequiredArgsConstructor
public class BonusPolicyController {
    private final BonusPolicyService bonusPolicyService;

    @Operation(summary = "적립 정책 추가 / 수정")
    @PostMapping
    public ResponseEntity<BonusPolicyDTO> addBonusPolicy(@RequestBody BonusPolicyDTO bonusPolicyDTO) {
        BonusPolicyDTO dto = bonusPolicyService.createBonusPolicy(bonusPolicyDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @Operation(summary = "적립 정책 종료 (23시 59분으로 지정 활성화 여부 FALSE)")
    @PatchMapping("/{id}/finish")
    public ResponseEntity<Void> finishBonusPolicy(@PathVariable Long id) {
        bonusPolicyService.finishActivation(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "적립 정책 만료여부 (딸깍)")
    @PatchMapping("finish-btn")
    public ResponseEntity<Void> finishBtn(@RequestBody ActivationRequest request) {
        bonusPolicyService.changeActivation(request);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "적립 정책 삭제")
    @DeleteMapping
    public ResponseEntity<Void> deleteBonusPolicy(@RequestParam Long id) {
        bonusPolicyService.deleteBonusPolicy(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "적림정책 전체 조회")
    @GetMapping("/list")
    public ResponseEntity<List<BonusPolicyDTO>> getBonusPolicies() {
        return ResponseEntity.ok(bonusPolicyService.getBonusPolicies());
    }

    @Operation(summary = "적립정책 단일 조회")
    @GetMapping("/{id}")
    public ResponseEntity<BonusPolicyDTO> getBonusPolicy(@PathVariable Long id) {
        return ResponseEntity.ok(bonusPolicyService.getBonusPolicy(id));
    }
}
