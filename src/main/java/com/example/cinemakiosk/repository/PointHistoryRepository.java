package com.example.cinemakiosk.repository;

import com.example.cinemakiosk.domain.PointHistory.PointHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {
}
