package com.example.cinemakiosk.service;

import com.example.cinemakiosk.dto.ReservationDetailsDTO;

import java.util.List;
import java.util.Map;

public interface ReservationService {
    //예매 진행
    ReservationDetailsDTO create(ReservationDetailsDTO reservationDetailsDTO);

    //특정 스케쥴 아이디에 대한 좌석 내역 전체 조회
    List<String> readAllReservationSeatByScheduleId(Long scheduleId);

    List<ReservationDetailsDTO> readSeatByMovieId(Long movieId);
}
