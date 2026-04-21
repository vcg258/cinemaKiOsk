package com.example.cinemakiosk.mapper;

import com.example.cinemakiosk.vo.ReservationDetailsVO;
import com.example.cinemakiosk.vo.ReservationSeatVO;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ReservationSeatMapper {
    //좌석 정보를 한번에 입력하는 메서드
    void insertSeats(ReservationDetailsVO reservationDetailsVO);
    //예매 고유번호를 이용해서 좌석 정보를 모두 가져 오는 메서드
    List<ReservationSeatVO> selectAllById(Long no);

    List<String> selectAllSeatByScheduleId(Long scheduleId);
}
