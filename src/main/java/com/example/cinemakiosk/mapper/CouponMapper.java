package com.example.cinemakiosk.mapper;

import com.example.cinemakiosk.vo.CouponVO;
import com.example.cinemakiosk.vo.DiscountPolicyVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CouponMapper {
    // 쿠폰 사용 검증 메서드
    CouponVO checkCoupon(String couponNum);

    //쿠폰 번호를 이용해서 대상을 찾는 코드
    CouponVO selectOneByNum(String num);

    // 쿠폰 전체 조회 (해당 할인정책 시작일, 만료일, 이름)
    List<CouponVO> selectAllCouponByDiscount(int page);

}
