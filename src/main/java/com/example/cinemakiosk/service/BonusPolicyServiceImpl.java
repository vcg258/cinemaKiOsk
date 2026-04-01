package com.example.cinemakiosk.service;

import com.example.cinemakiosk.domain.BonusPolicyEntity;
import com.example.cinemakiosk.dto.BonusPolicyDTO;
import com.example.cinemakiosk.repository.BonusPolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BonusPolicyServiceImpl implements BonusPolicyService{
    private final BonusPolicyRepository bonusPolicyRepository;

    // 등록
    @Override
    public void addBonusPolicy(BonusPolicyDTO bonusPolicyDTO) {
        BonusPolicyEntity bonusPolicyEntity = BonusPolicyDTO.toEntity(bonusPolicyDTO);
        bonusPolicyRepository.save(bonusPolicyEntity);
    }

    // 정책 전체 조회
    @Override
    public List<BonusPolicyDTO> getBonusPolicies() {
        List<BonusPolicyEntity> bonusPolicyEntityList = bonusPolicyRepository.findAllWithPayments();
        List<BonusPolicyDTO> bonusPolicyDTOList = new ArrayList<>();

        for (BonusPolicyEntity bonusPolicyEntity : bonusPolicyEntityList) {
            bonusPolicyDTOList.add(BonusPolicyEntity.toDTO(bonusPolicyEntity));
        }

        return bonusPolicyDTOList;
    }
}
