package com.example.cinemakiosk.repository;

import com.example.cinemakiosk.domain.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberRepository extends JpaRepository<MemberEntity, String> {
    // 기존 회원인지 유무 확인
    boolean existsByPhone(String phone);

}
