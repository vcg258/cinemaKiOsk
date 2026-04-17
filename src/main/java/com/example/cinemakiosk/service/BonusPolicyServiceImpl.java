package com.example.cinemakiosk.service;

import com.example.cinemakiosk.domain.BonusPolicyEntity;
import com.example.cinemakiosk.dto.BonusPolicyDTO;
import com.example.cinemakiosk.dto.RequestDTO.ActivationRequest;
import com.example.cinemakiosk.mapper.BonusPolicyMapper;
import com.example.cinemakiosk.repository.BonusPolicyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class BonusPolicyServiceImpl implements BonusPolicyService {
    private final BonusPolicyRepository bonusPolicyRepository;
    private final BonusPolicyMapper bonusPolicyMapper;

    /**
     * 적립정책 추가 / 수정
     * @param bonusPolicyDTO 활인정책 DTO
     */
    @Override
    public void createBonusPolicy(BonusPolicyDTO bonusPolicyDTO) {
        if (bonusPolicyRepository.existsByPolicyNameAndEndAtAfter(bonusPolicyDTO.getPolicyName(), LocalDateTime.now())) {
            throw new IllegalStateException("createBonusPolicy... 활성화된 정책중 이름이 중복됩니다 추가 / 수정 실패");
        }

        BonusPolicyDTO dto = BonusPolicyDTO.builder()
                .policyName(bonusPolicyDTO.getPolicyName())
                .giveValue(bonusPolicyDTO.getGiveValue())
                .startAt(bonusPolicyDTO.getStartAt())
                .endAt(bonusPolicyDTO.getEndAt())
                .activation(bonusPolicyDTO.getActivation())
                .build();

        bonusPolicyRepository.save(BonusPolicyDTO.toEntity(dto));
        log.info("createBonusPolicy... 할인정책 추가/ 수정 성공 : {}", dto);
    }

    /**
     * 적립정책 종료 (23시 59분으로 지정 활성화 여부 FALSE)
     * @param id 적립 정책 PK
     */
    @Override
    public void finishActivation(Long id) { // TODO batch 사용으로 만료시간이 되면 자동 비활성화로 변경 해야함
        BonusPolicyEntity bonusPolicyEntity = bonusPolicyRepository.findById(id).orElseThrow();
        if (LocalDateTime.now().isAfter(bonusPolicyEntity.getEndAt()) || !bonusPolicyEntity.getActivation()) {
            throw new IllegalStateException("적립정책이 이미 비활성화임 종료 지정 불가능");
        }

        bonusPolicyEntity.changeEndAt(LocalDateTime.now().withHour(23).withMinute(59).withSecond(59));
        bonusPolicyRepository.save(bonusPolicyEntity);
        log.info("finishActivation... {}", bonusPolicyEntity);
    }

    /**
     * 적립정책 만료여부
     * @param request 요청 DTO
     */
    @Override
    public void changeActivation(ActivationRequest request) {
        List<BonusPolicyEntity> bonusPolicyEntities = bonusPolicyRepository.findAllById(request.getIds());
        bonusPolicyEntities.forEach(bonusPolicyEntity -> {
            if (bonusPolicyEntity.getActivation() == request.isActivation()) {
                log.warn("이미 같은 상태값 변경 안됨 {}", bonusPolicyEntity);
                return;
            }
            bonusPolicyEntity.changeActivation(request.isActivation());
            log.info("changeActivation... 만료여부 변경 : {}", bonusPolicyEntity);
        });
        bonusPolicyRepository.saveAll(bonusPolicyEntities);
    }

    /**
     * 적립정책 전체 조회
     * @return 전체 적립정책을 담은 리스트
     */
    @Override
    public List<BonusPolicyDTO> getBonusPolicies() {
        List<BonusPolicyEntity> policyEntities = bonusPolicyRepository.findAllByEndAtAfter(LocalDateTime.now());
        return policyEntities.stream().map(BonusPolicyEntity::toDTO).toList();
    }

    /**
     * 적립정책 단일 조회
     * @param id 적립정책 PK
     * @return 지정 적립정책
     */
    @Override
    public BonusPolicyDTO getBonusPolicy(Long id) {
        BonusPolicyEntity policy = bonusPolicyRepository.findById(id).orElseThrow();
        return BonusPolicyEntity.toDTO(policy);
    }

    /**
     * 10페이지씩 페이징 처리 (로그형식 전체)
     * @param page 몇번째 페이지 부터 정할 변수
     * @return 페이징 결과 1페이지 일경우 1 ~ 10번 까지
     */
    @Override
    public Page<BonusPolicyDTO> getBonusPolicyPage(int page) {
        Pageable pageable = PageRequest.of(page - 1, 10, Sort.by("id").descending());
        Page<BonusPolicyEntity> entityPage = bonusPolicyRepository.findAll(pageable);
        return entityPage.map(BonusPolicyEntity::toDTO);
    }
}
