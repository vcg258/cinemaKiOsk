package com.example.cinemakiosk.mapper;

import com.example.cinemakiosk.domain.MovieEntity.MovieEntity;
import com.example.cinemakiosk.domain.MovieEntity.Rating;
import com.example.cinemakiosk.dto.DiscountPolicyDTO;
import com.example.cinemakiosk.dto.MovieDTO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Mapper
public interface MovieMapper {

    List<MovieEntity> findBySeveral(@Param("keyWord") String keyWord,
                                    @Param("genre") String genre,
                                    @Param("rating") Rating rating);



}

