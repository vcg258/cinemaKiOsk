package com.example.cinemakiosk.repository;

import com.example.cinemakiosk.domain.SeatPolicyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatPolicyRepository extends JpaRepository<SeatPolicyEntity, Long> {
    // 좌석 이름 중복 금지를 위한 검사 메서드
    boolean existsByName(String name);
}
