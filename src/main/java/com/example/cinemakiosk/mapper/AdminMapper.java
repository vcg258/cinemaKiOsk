package com.example.cinemakiosk.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

@Mapper
public interface AdminMapper {
    int updateUuid(@Param("adminId") Long adminId, @Param("uuid") String uuid);

    int clearUuid(@Param("adminId") Long adminId);
}
