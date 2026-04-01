package com.example.cinemakiosk.mapper;

import com.example.cinemakiosk.vo.SeatPolicyVO;
import org.apache.ibatis.annotations.Mapper;import java.util.List;

@Mapper
public interface SeatPolicyMapper {
    SeatPolicyVO selectOneById(Long no);

    List<SeatPolicyVO> selectAll();
}
