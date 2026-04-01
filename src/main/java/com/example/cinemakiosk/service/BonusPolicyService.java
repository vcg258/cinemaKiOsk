package com.example.cinemakiosk.service;

import com.example.cinemakiosk.domain.BonusPolicyEntity;
import com.example.cinemakiosk.dto.BonusPolicyDTO;

import java.util.List;

public interface BonusPolicyService {

    // 등록
    void addBonusPolicy(BonusPolicyDTO BonusPolicyDTO);

    // 전체 조회
    List<BonusPolicyDTO> getBonusPolicies();
}
