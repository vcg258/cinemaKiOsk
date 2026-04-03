package com.example.cinemakiosk.service;

import com.example.cinemakiosk.dto.SeatPolicyDTO;
import com.example.cinemakiosk.dto.TheaterDTO;

import java.util.List;

public interface TheaterService {

    // 상영관 등록 (대공사를 할 수 도있으니까 일단 넣어놔봄)
    void createTheater(TheaterDTO theaterDTO);

    // 상영관 전체 조회
    List<TheaterDTO> getTheaterAll();

    // 상영관 단일 조회
    TheaterDTO getTheater(Long no);

    // 상영관 좌석 정책 업데이트
    void updateSeatPolicy(TheaterDTO theaterDTO);

    // 상영관 청소시간 업데이트
    void updateCleanTime(TheaterDTO theaterDTO);

    // 좌석 정책 생성
    void createSeat(SeatPolicyDTO seatPolicyDTO);

    // 좌석 정책 전체를 확인
    List<SeatPolicyDTO> readAllSeat();

    // 좌석 정책 1개를 확인
    SeatPolicyDTO readSeat(Long no);

    // 좌석 정책 수정
    void updateSeat(SeatPolicyDTO seatPolicyDTO);

    // 좌석 정책 단일 삭제
    void deleteSeat(Long no);
}
