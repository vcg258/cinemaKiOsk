package com.example.cinemakiosk.mapper;

import com.example.cinemakiosk.vo.ReservationDetailsVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ReservationDetailsMapper {

    ReservationDetailsVO selectOneById(Long no);

}
