package com.example.cinemakiosk.mapper;

import com.example.cinemakiosk.vo.TheaterVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TheaterMapper {
    //id로 상영관 정보를 1개 조회
    TheaterVO selectOneById(Long no);

    //상영관 정보를 전체 조회
    List<TheaterVO> selectAll();
}
