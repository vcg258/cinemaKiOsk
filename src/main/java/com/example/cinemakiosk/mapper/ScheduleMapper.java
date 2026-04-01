package com.example.cinemakiosk.mapper;

import com.example.cinemakiosk.vo.ScheduleVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ScheduleMapper {
    ScheduleVO selectOneById(Long no);

    List<ScheduleVO> selectAll();

}
