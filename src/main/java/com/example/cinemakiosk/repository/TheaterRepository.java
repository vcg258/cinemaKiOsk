package com.example.cinemakiosk.repository;

import com.example.cinemakiosk.domain.Theater;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TheaterRepository extends JpaRepository<Theater, Long> {
}
