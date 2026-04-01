package com.example.cinemakiosk.mapper;

import com.example.cinemakiosk.vo.TheaterVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TheaterMapper {
    TheaterVO selectOneById(Long no);

    List<TheaterVO> selectAll();
}
