package com.example.cinemakiosk.mapper;

import com.example.cinemakiosk.vo.CouponVO;
import com.example.cinemakiosk.vo.DiscountPolicyVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CouponMapper {
    // 쿠폰 사용 검증 메서드
    CouponVO checkCoupon(String couponNum);

    //쿠폰 번호를 이용해서 대상을 찾는 코드
    CouponVO selectOneByNum(String num);

}
