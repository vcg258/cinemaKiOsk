package com.example.cinemakiosk.service;

import com.example.cinemakiosk.dto.PointHistoryDTO;

public interface MemberService {
    // 신규 회원등록
    void createMember(String phone, Integer point, String paymentId);

    // 포인트 보유포인트 처리 내역
    void pointHistoryCreate(PointHistoryDTO pointHistoryDTO);

    // 환불할 경우 포인트 내역
    void pointHistoryCancel(PointHistoryDTO pointHistoryDTO);
}
