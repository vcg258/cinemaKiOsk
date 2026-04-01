package com.example.cinemakiosk.repository;

import com.example.cinemakiosk.domain.BonusPolicyEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BonusPolicyRepository extends JpaRepository<BonusPolicyEntity, Long>{
    @Query("SELECT DISTINCT b FROM BonusPolicyEntity b LEFT JOIN FETCH b.paymentDetailsEntity")
    List<BonusPolicyEntity> findAllWithPayments();
}

