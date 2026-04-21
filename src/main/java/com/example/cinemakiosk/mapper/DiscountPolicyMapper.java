package com.example.cinemakiosk.mapper;

import com.example.cinemakiosk.dto.DiscountPolicyDTO;
import com.example.cinemakiosk.vo.DiscountPolicyVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DiscountPolicyMapper {
    //할인 정책으로 1개의 정보를 조회하는 메서드
    DiscountPolicyVO selectOneById(Long no);

    //할인 정책 전부를 조회하는 메서드
    List<DiscountPolicyVO> selectAll();
}
