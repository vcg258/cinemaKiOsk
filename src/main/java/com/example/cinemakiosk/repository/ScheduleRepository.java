package com.example.cinemakiosk.repository;

import com.example.cinemakiosk.domain.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
}
