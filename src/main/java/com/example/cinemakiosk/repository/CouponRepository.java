package com.example.cinemakiosk.repository;

import com.example.cinemakiosk.domain.CouponEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRepository extends JpaRepository<CouponEntity, String> {
}
