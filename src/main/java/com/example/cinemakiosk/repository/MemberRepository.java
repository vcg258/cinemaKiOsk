package com.example.cinemakiosk.repository;

import com.example.cinemakiosk.domain.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {
}
