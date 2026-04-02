package com.example.cinemakiosk.mapper;

import com.example.cinemakiosk.vo.SeatPolicyVO;
import org.apache.ibatis.annotations.Mapper;import java.util.List;

@Mapper
public interface SeatPolicyMapper {
    //id로 좌석 정책 1개를 조회
    SeatPolicyVO selectOneById(Long no);
    //좌석 정책 전체를 조회
    List<SeatPolicyVO> selectAll();
}
