package com.example.cinemakiosk.mapper;

import com.example.cinemakiosk.domain.StatisticsEntity.StatisticsEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface StatisticsMapper {

    List<StatisticsEntity> findByMonth(@Param("year") int year, @Param("month") int month);

    List<StatisticsEntity> findByHour(int hour);

}
