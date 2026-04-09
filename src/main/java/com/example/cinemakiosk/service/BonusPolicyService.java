package com.example.cinemakiosk.service;

import com.example.cinemakiosk.dto.BonusPolicyDTO;
import com.example.cinemakiosk.dto.RequestDTO.ActivationRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface BonusPolicyService {
    // 적립 정책 추가 / 수정
    BonusPolicyDTO createBonusPolicy(BonusPolicyDTO bonusPolicyDTO);

    // 적립 정책 종료 (23시 59분으로 지정 활성화 여부 FALSE)
    void finishActivation(Long id);

    // 적립 정책 만료여부 (딸깍)
    void changeActivation(ActivationRequest request);

    // 적립 정책 삭제
    void deleteBonusPolicy(Long id);

    // 할인정책 전체 조회
    List<BonusPolicyDTO> getBonusPolicies();

    // 할인정책 단일 조회
    BonusPolicyDTO getBonusPolicy(Long id);

    // 페이징 처리
    Page<BonusPolicyDTO> getBonusPolicyPage(int page);
}
