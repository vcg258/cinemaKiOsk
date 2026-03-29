package com.example.cinemakiosk.repository;

import com.example.cinemakiosk.domain.TheaterEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TheaterRepository extends JpaRepository<TheaterEntity, Long> {
    // 활성화 중인 좌석정책을 삭제 하는것을 방지하기 위한 검증 메서드
    boolean existsBySeatPolicyEntity_PolicyId(Long policyId);
}
