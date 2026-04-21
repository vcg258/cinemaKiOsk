package com.example.cinemakiosk.mapper;

import com.example.cinemakiosk.vo.AdminVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AdminMapper {
    // 전체 관리자조회 및 관리자에 해당하는 권한 모두 조회
    List<AdminVO> selectAdminByAdminRole();
}
