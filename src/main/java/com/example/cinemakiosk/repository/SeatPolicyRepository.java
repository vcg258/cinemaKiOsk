package com.example.cinemakiosk.repository;

import com.example.cinemakiosk.domain.SeatPolicyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatPolicyRepository extends JpaRepository<SeatPolicyEntity, Long> {
}
