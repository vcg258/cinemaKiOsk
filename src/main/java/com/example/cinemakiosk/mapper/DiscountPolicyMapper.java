package com.example.cinemakiosk.mapper;

import com.example.cinemakiosk.vo.DiscountPolicyVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DiscountPolicyMapper {
    DiscountPolicyVO selectById(long id);
}
