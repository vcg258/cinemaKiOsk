package com.example.cinemakiosk.repository;

import com.example.cinemakiosk.domain.PointHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PointHistoryRepository extends JpaRepository<PointHistoryEntity, Long> {
    // 지정 회원의 전체 포인트 내역 조회
    List<PointHistoryEntity> findByMemberEntity_PhoneOrderByCreateAtDesc(String phone);

    // 삭제 회원(DEL_ 시작) 제외한 포인트 내역 수 조회 (페이징 count 전용)
    long countByMemberEntityPhoneNotLike(String keyword);
}
