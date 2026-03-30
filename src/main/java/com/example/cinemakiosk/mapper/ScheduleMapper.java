package com.example.cinemakiosk.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

@Mapper
public interface ScheduleMapper {
    // 스케줄 등록시 같은 상영관에서 시간이 겹치는지 검증하는 메서드
    int checkScheduleOverlap(@Param("no") Long no, @Param("startAt") LocalDateTime startAt, @Param("endAt") LocalDateTime endAt);
}
