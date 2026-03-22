package com.example.cinemakiosk.repository;

import com.example.cinemakiosk.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
