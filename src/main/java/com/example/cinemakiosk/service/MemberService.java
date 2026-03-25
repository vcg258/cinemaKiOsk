package com.example.cinemakiosk.service;

import com.example.cinemakiosk.domain.PointHistoryEntity.Type;

public interface MemberService {
    // 신규 회원등록
    void createMember(String phone, Integer point, String paymentId);

    // 포인트 보유포인트 처리 내역
    void pointHistoryCreate(String phone, Integer point, Type type, String paymentId);

    // 환불할 경우 포인트 내역
    void pointHistoryCancel(Long no);
}
