package com.example.cinemakiosk.repository;

import com.example.cinemakiosk.domain.TheaterEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TheaterRepository extends JpaRepository<TheaterEntity, Long> {
}
