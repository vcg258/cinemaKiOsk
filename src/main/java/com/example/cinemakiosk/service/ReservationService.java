package com.example.cinemakiosk.service;

import com.example.cinemakiosk.dto.MovieDTO;
import com.example.cinemakiosk.dto.ReservationDetailsDTO;
import com.example.cinemakiosk.dto.ReservationSeatDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ReservationService {
    //예매 진행
    public void create(ReservationDetailsDTO reservationDetailsDTO);

    //예매 내역 조회
    public ReservationDetailsDTO read(Long no);

    //예매 내역 전체 조회
    public List<ReservationDetailsDTO> readAll();

    //특정 스케쥴 아이디에 대한 좌석 내역 전체 조회
    public List<String> readAllSeatByScheduleId(Long scheduleId);

    //예매 내역 변경.
    public void update(ReservationDetailsDTO reservationDetailsDTO);

    //유효기간 지난 예매 삭제
    public void delete(Long no);

    // 페이징
    Page<ReservationDetailsDTO> getReservationDetailsPage(int page);


}
