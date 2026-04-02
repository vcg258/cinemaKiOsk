package com.example.cinemakiosk.mapper;

import com.example.cinemakiosk.vo.ScheduleVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ScheduleMapper {
    //id를 이용해서 스케쥴을 조회
    ScheduleVO selectOneById(Long no);
    //스케줄을 전체 조회
    List<ScheduleVO> selectAll();

}
