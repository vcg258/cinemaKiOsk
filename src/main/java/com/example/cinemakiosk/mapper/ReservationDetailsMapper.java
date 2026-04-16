package com.example.cinemakiosk.mapper;

import com.example.cinemakiosk.vo.ReservationDetailsVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ReservationDetailsMapper {
    //id로 예약내역을 1개 조회
    ReservationDetailsVO selectOneById(String no);
    //예약내역을 전체 조회
    List<ReservationDetailsVO> selectAll();

    List<ReservationDetailsVO> selectAllByMovieId(Long movieId);
}
