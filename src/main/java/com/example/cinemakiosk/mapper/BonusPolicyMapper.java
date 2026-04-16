package com.example.cinemakiosk.mapper;

import com.example.cinemakiosk.vo.BonusPolicyVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BonusPolicyMapper {
    //보너스 아이디를 이용해서 찾아오는 mapper
    BonusPolicyVO selectOneById(@Param("no") Long no);
}
