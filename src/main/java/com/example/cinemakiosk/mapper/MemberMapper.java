package com.example.cinemakiosk.mapper;

import com.example.cinemakiosk.vo.MemberVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MemberMapper {
    MemberVO selectOneById(String phone);

    List<MemberVO> selectAll();
}
