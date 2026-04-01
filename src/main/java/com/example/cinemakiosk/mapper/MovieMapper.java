package com.example.cinemakiosk.mapper;

import com.example.cinemakiosk.domain.MovieEntity;
import com.example.cinemakiosk.domain.enums.Rating;
import com.example.cinemakiosk.vo.MovieVO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Mapper
public interface MovieMapper {

    //왜 mapper에서 entity를 쓰지?
    List<MovieEntity> findBySeveral(@Param("keyWord") String keyWord,
                                    @Param("genre") String genre,
                                    @Param("rating") Rating rating);

    MovieVO selectOneById(Long no);

    List<MovieVO> selectAll();

}

