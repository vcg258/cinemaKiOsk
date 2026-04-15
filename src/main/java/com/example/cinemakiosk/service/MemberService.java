package com.example.cinemakiosk.service;

import com.example.cinemakiosk.dto.MemberDTO;
import com.example.cinemakiosk.dto.PointHistoryDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface MemberService {
    // 신규 회원등록
    void createMember(MemberDTO memberDTO);

    // 포인트 보유포인트 처리 내역
    void pointHistoryCreate(PointHistoryDTO pointHistoryDTO);

    // 환불할 경우 포인트 내역
    void pointHistoryCancel(PointHistoryDTO pointHistoryDTO);

    // 회원 전체 조회 (페이징)
    Page<MemberDTO> getMembersAll(int page);

    // 지정 회원 전체 로그 조회
    List<PointHistoryDTO> getMembersAllLog(String phone);

    // 회원 단일 조회
    MemberDTO getMember(String phone);

    // 전체 포인트 조회 TODO Mapper로 페이징처리 해야함 어휴
    List<PointHistoryDTO> getPointHistoryAll();
}
