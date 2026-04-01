package com.example.cinemakiosk.repository;

import com.example.cinemakiosk.domain.ScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<ScheduleEntity, Long> {
    // 스케줄에서 지정영화만 전체 조회하기 위한 메서드
    List<ScheduleEntity> findByMovieEntity_MovieId(Long movieId);
}
