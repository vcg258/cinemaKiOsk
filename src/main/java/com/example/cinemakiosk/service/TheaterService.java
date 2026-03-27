package com.example.cinemakiosk.service;

import com.example.cinemakiosk.dto.SeatPolicyDTO;
import com.example.cinemakiosk.dto.TheaterDTO;

import java.util.List;

public interface TheaterService {
    // 좌석 정책 생성
    void createSeat(SeatPolicyDTO seatPolicyDTO);

    // 좌석 정책 1개를 확인
    TheaterDTO readSeat(Long no);

    // 좌석 정책 전체를 확인
    List<TheaterDTO> readAllSeat();

    // 좌석 정책 1개를 수정
    void updateSeat(SeatPolicyDTO seatPolicyDTO);

    // 좌석 정책 1개를 삭제
    void deleteSeat(Long no);
}
