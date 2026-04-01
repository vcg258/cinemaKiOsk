package com.example.cinemakiosk.mapper;

import com.example.cinemakiosk.vo.BonusPolicyVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BonusPolicyMapper {
    BonusPolicyVO selectOneById(Long no);
}
