package com.example.cinemakiosk.mapper;

import com.example.cinemakiosk.vo.ReservationDetailsVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ReservationDetailsMapper {

    ReservationDetailsVO selectOneById(Long no);

    List<ReservationDetailsVO> selectAll();

}
