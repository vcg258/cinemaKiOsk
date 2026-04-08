package com.example.cinemakiosk.mapper;

import com.example.cinemakiosk.dto.PointHistoryDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PointHistoryMapper {
    // 포인트 적립이 된 영화명까지 포함
    List<PointHistoryDTO> selectByMovieNameAll();
}
