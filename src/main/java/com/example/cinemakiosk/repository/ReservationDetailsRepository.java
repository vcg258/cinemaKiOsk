package com.example.cinemakiosk.repository;

import com.example.cinemakiosk.domain.ReservationDetailsEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ReservationDetailsRepository extends JpaRepository<ReservationDetailsEntity, Long>{

}

