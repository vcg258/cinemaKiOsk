package com.example.cinemakiosk.repository;

import com.example.cinemakiosk.domain.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRepository extends JpaRepository<Coupon, Integer> {
}
