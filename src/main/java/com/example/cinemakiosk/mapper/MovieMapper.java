package com.example.cinemakiosk.mapper;

import com.example.cinemakiosk.vo.MovieVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MovieMapper {
    //영화 id로 1개를 찾음.
    MovieVO selectOneById(Long no);
    //영화 목록 전체를 찾아옴.
    List<MovieVO> selectAll();

}

