package com.example.cinemakiosk.service;

import com.example.cinemakiosk.domain.enums.Grade;
import com.example.cinemakiosk.dto.BonusPolicyDTO;
import com.example.cinemakiosk.dto.CouponDTO;
import com.example.cinemakiosk.dto.DiscountPolicyDTO;
import com.example.cinemakiosk.dto.MemberDTO;
import com.example.cinemakiosk.dto.MovieDTO;
import com.example.cinemakiosk.dto.ScheduleDTO;
import com.example.cinemakiosk.dto.SeatPolicyDTO;
import com.example.cinemakiosk.dto.TheaterDTO;

import java.util.List;

public interface CustomerService {
    // 상영관 전체 조회 (고객용)
    List<TheaterDTO> getTheaterAll();

    // 좌석 정책 전체 조회 (고객용)
    List<SeatPolicyDTO> readAllSeat();

    // 영화 단일 조회 (고객 상세 페이지용)
    MovieDTO getMovieById(Long movieId);

    // 현재 상영중인 영화 전체 조회 (고객용)
    List<MovieDTO> getScreeningPeriodAllMovies();

    // 적립정책 전체 조회 (고객용)
    List<BonusPolicyDTO> getBonusPolicies();

    // 쿠폰 사용 검증 (고객용)
    CouponDTO authCoupon(String couponNum);

    // 연령 할인 정책 조회 (고객용)
    List<DiscountPolicyDTO> getAgeDiscounts();

    // 시간 할인 정책 조회 (고객용)
    List<DiscountPolicyDTO> getTimeDiscounts();

    // 회원 가입 (고객용)
    void createMember(MemberDTO memberDTO);

    // 회원 단일 조회 (고객용)
    MemberDTO getMember(String phone);

    // 스케줄 전체 조회 (고객용)
    List<ScheduleDTO> getScheduleList();

    // 스케줄 전체 조회 - Mapper 버전 (고객용)
    List<ScheduleDTO> getScheduleDTOList();

    // 지정 영화의 전체 스케줄 조회 (고객용)
    List<ScheduleDTO> getScheduleListByMovieWithCustomer(Long movieId);

    // 상영관 단일 조회 (내부 전용 - 고객 결제/관리자 즉석예매 공용, 권한 검증 없음)
    TheaterDTO getTheater(Long no);

    // 좌석정책 단일 조회 (내부 전용 - 고객 결제/관리자 즉석예매 공용, 권한 검증 없음)
    SeatPolicyDTO readSeat(Long policyId);

    // 쿠폰 단일 조회 (내부 전용 - 고객 결제 공용, 권한 검증 없음)
    CouponDTO getCoupon(String couponNum);

    // 적립정책 단일 조회 (내부 전용 - 고객 결제 공용, 권한 검증 없음)
    BonusPolicyDTO getBonusPolicy(Grade grade);

    // 상영관 전체 조회 - Mapper 버전 (고객용, 권한 검증 없음)
    List<TheaterDTO> getTheaterDTOAll();
}
