package com.example.cinemakiosk.mapper;

import com.example.cinemakiosk.vo.MemberVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MemberMapper {
    //핸드폰 번호로 회원 정보를 조회하는 기능.
    MemberVO selectOneById(String phone);
    //회원 전체를 조회하는 기능.
    List<MemberVO> selectAll();
}
