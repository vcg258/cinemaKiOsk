package com.example.cinemakiosk.mapper;

import com.example.cinemakiosk.dto.DiscountPolicyDTO;
import com.example.cinemakiosk.vo.DiscountPolicyVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DiscountPolicyMapper {
    // 쿠폰 사용 검증 메서드
    DiscountPolicyDTO checkCoupon(Long id);
}
