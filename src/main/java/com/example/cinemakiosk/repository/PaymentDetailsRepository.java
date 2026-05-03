package com.example.cinemakiosk.repository;

import com.example.cinemakiosk.domain.PaymentDetailsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentDetailsRepository extends JpaRepository<PaymentDetailsEntity, String>{
}

