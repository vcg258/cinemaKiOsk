package com.example.cinemakiosk.mapper;

import com.example.cinemakiosk.domain.MovieEntity;
import com.example.cinemakiosk.domain.enums.Rating;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Mapper
public interface MovieMapper {

    List<MovieEntity> findBySeveral(@Param("keyWord") String keyWord,
                                    @Param("genre") String genre,
                                    @Param("rating") Rating rating);



}

