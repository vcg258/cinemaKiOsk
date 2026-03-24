package com.example.cinemakiosk.repository;

import com.example.cinemakiosk.domain.PointHistoryEntity.PointHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointHistoryRepository extends JpaRepository<PointHistoryEntity, Long> {
}
