package com.example.cinemakiosk.repository;

import com.example.cinemakiosk.domain.MemberEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberRepository extends JpaRepository<MemberEntity, String> {
    // 기존 회원인지 유무 확인
    boolean existsByPhone(String phone);

    // 삭제된 회원(DEL_ 시작하는 회원) 조회제외
    Page<MemberEntity> findByPhoneNotLike(String keyWord , Pageable pageable);

}
