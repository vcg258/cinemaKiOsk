package com.example.cinemakiosk.repository;

import com.example.cinemakiosk.domain.AdminEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<AdminEntity, Long> {
}
