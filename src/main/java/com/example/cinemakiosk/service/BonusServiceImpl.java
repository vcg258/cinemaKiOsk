package com.example.cinemakiosk.service;

import com.example.cinemakiosk.domain.BonusPolicyEntity;
import com.example.cinemakiosk.dto.BonusPolicyDTO;
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
public class BonusServiceImpl implements BonusService {
    private final BonusPolicyRepository bonusPolicyRepository;
    private final BonusPolicyMapper bonusPolicyMapper;

    /**
     * 할인정책 추가 / 수정
     * @param bonusPolicyDTO 활인정책 DTO
     */
    @Override
    public void createBonusPolicy(BonusPolicyDTO bonusPolicyDTO) {
        if (bonusPolicyRepository.existsByPolicyNameAndEndAtAfter(bonusPolicyDTO.getPolicyName(), LocalDateTime.now())) {
            log.error("createBonusPolicy... 활성화된 정책중 이름이 중복됩니다 추가 / 수정 실패");
            return;
        }

        BonusPolicyDTO dto = BonusPolicyDTO.builder()
                .policyName(bonusPolicyDTO.getPolicyName())
                .giveValue(bonusPolicyDTO.getGiveValue())
                .startAt(bonusPolicyDTO.getStartAt())
                .finishedAt(bonusPolicyDTO.getFinishedAt())
                .activation(bonusPolicyDTO.getActivation())
                .build();

        bonusPolicyRepository.save(BonusPolicyDTO.toEntity(dto));
        log.info("createBonusPolicy... 할인정책 추가/ 수정 성공 : {}", dto);
    }

    /**
     * 적립 정책 종료 (23시 59분으로 지정 활성화 여부 FALSE)
     * @param id 적립 정책 PK
     */
    @Override
    public void finishActivation(Long id) { // TODO batch 사용으로 만료시간이 되면 자동 비활성화로 변경 해야함
        BonusPolicyEntity bonusPolicyEntity = bonusPolicyRepository.findById(id).orElseThrow();
        if (LocalDateTime.now().isAfter(bonusPolicyEntity.getEndAt()) || !bonusPolicyEntity.getActivation()) {
            log.error("finishActivation... 이미 비활성화 된 정책입니다.");
        }

        bonusPolicyEntity.changeEndAt(LocalDateTime.now().withHour(23).withMinute(59).withSecond(59));
        bonusPolicyRepository.save(bonusPolicyEntity);
        log.info("finishActivation... {}", bonusPolicyEntity);
    }

    /**
     * 할인정책 만료여부
     * @param id 할인정책 PK
     * @param activation 만료여부 지정
     */
    @Override
    public void changeActivation(Long id, boolean activation) {
        BonusPolicyEntity policy = bonusPolicyRepository.findById(id).orElseThrow();
        policy.changeActivation(activation);
        log.info("changeActivation... 만료여부 변경 : {}", policy);
        bonusPolicyRepository.save(policy);
    }

    /**
     * 할인 정책 전체 조회
     * @return 전체 할인정책을 담은 리스트
     */
    @Override
    public List<BonusPolicyDTO> getBonusPolicies() {
        List<BonusPolicyEntity> policyEntities = bonusPolicyRepository.findAll();
        return policyEntities.stream().map(BonusPolicyEntity::toDTO).toList();
    }

    /**
     * 할인 정책 단일 조회
     * @param id 할인정책 PK
     * @return 지정 할인 정책
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
