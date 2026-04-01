package com.example.cinemakiosk.mapper;

import com.example.cinemakiosk.vo.SeatPolicyVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SeatPolicyMapper {
    void insert(SeatPolicyVO seatPolicyVO);

    SeatPolicyVO selectOne(Long no);

    SeatPolicyVO selectAll();


}
