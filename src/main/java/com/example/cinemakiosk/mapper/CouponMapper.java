package com.example.cinemakiosk.mapper;

import com.example.cinemakiosk.vo.CouponVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CouponMapper {
    //쿠폰 번호를 이용해서 대상을 찾는 코드
    CouponVO selectOneByNum(String num);

}
