package com.example.cinemakiosk.mapper;

import com.example.cinemakiosk.domain.MovieEntity;
import com.example.cinemakiosk.domain.enums.Rating;
import com.example.cinemakiosk.vo.MovieVO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Mapper
public interface MovieMapper {

    //왜 mapper에서 entity를 쓰지? 심지어 mapper 안에서는 DTO를 반환함.
//    List<MovieEntity> findBySeveral(@Param("keyWord") String keyWord,
//                                    @Param("genre") String genre,
//                                    @Param("rating") Rating rating);
    //영화 id로 1개를 찾음.
    MovieVO selectOneById(Long no);
    //영화 목록 전체를 찾아옴.
    List<MovieVO> selectAll();

}

