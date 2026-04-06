package com.example.cinemakiosk.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

@Mapper
public interface AdminMapper {
    /**
     * 자동로그인 UUID를 등록하는 기능
     * @param adminId
     * @param uuid
     * @return
     */
    int updateUuid(@Param("adminId") Long adminId, @Param("uuid") String uuid);

    /**
     * 자동로그인 UUID를 제거하는 기능
     * @param adminId
     * @return
     */
    int clearUuid(@Param("adminId") Long adminId);
}
