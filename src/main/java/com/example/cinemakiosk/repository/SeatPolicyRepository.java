package com.example.cinemakiosk.repository;

import com.example.cinemakiosk.domain.SeatPolicy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatPolicyRepository extends JpaRepository<SeatPolicy, Long> {
}
