package com.example.cinemakiosk.repository;

import com.example.cinemakiosk.domain.DiscountPolicy.DiscountPolicy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiscountPolicyRepository extends JpaRepository<DiscountPolicy, Long> {

}
