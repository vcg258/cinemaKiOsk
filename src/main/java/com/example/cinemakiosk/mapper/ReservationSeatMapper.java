package com.example.cinemakiosk.mapper;

import com.example.cinemakiosk.vo.ReservationDetailsVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ReservationSeatMapper {
    void insertSeats(ReservationDetailsVO reservationDetailsVO);
//해당 매퍼는 사용 안함.
}
