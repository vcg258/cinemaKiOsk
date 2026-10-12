package com.example.cinemakiosk.service;

import com.example.cinemakiosk.domain.BonusPolicyEntity;
import com.example.cinemakiosk.domain.CouponEntity;
import com.example.cinemakiosk.domain.DiscountPolicyEntity;
import com.example.cinemakiosk.domain.MemberEntity;
import com.example.cinemakiosk.domain.MovieEntity;
import com.example.cinemakiosk.domain.ScheduleEntity;
import com.example.cinemakiosk.domain.SeatPolicyEntity;
import com.example.cinemakiosk.domain.TheaterEntity;
import com.example.cinemakiosk.domain.enums.ConditionType;
import com.example.cinemakiosk.domain.enums.Grade;
import com.example.cinemakiosk.dto.BonusPolicyDTO;
import com.example.cinemakiosk.dto.CouponDTO;
import com.example.cinemakiosk.dto.DiscountPolicyDTO;
import com.example.cinemakiosk.dto.MemberDTO;
import com.example.cinemakiosk.dto.MovieDTO;
import com.example.cinemakiosk.dto.ScheduleDTO;
import com.example.cinemakiosk.dto.SeatPolicyDTO;
import com.example.cinemakiosk.dto.TheaterDTO;
import com.example.cinemakiosk.mapper.CouponMapper;
import com.example.cinemakiosk.mapper.ScheduleMapper;
import com.example.cinemakiosk.mapper.TheaterMapper;
import com.example.cinemakiosk.repository.BonusPolicyRepository;
import com.example.cinemakiosk.repository.CouponRepository;
import com.example.cinemakiosk.repository.DiscountPolicyRepository;
import com.example.cinemakiosk.repository.MemberRepository;
import com.example.cinemakiosk.repository.MovieRepository;
import com.example.cinemakiosk.repository.ScheduleRepository;
import com.example.cinemakiosk.repository.SeatPolicyRepository;
import com.example.cinemakiosk.repository.TheaterRepository;
import com.example.cinemakiosk.vo.CouponVO;
import com.example.cinemakiosk.vo.ScheduleVO;
import com.example.cinemakiosk.vo.TheaterVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * 고객(비로그인) 전용 서비스 구현체
 * - 관리자용 서비스(TheaterServiceImpl, MovieServiceImpl, BonusPolicyServiceImpl,
 *   DiscountPolicyServiceImpl, MemberServiceImpl, ScheduleServiceImpl)와
 *   동일한 조회 로직을 Repository/Mapper에서 직접 가져옴 (권한 검증 로직은 포함하지 않음)
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final TheaterRepository theaterRepository;
    private final SeatPolicyRepository seatPolicyRepository;
    private final MovieRepository movieRepository;
    private final BonusPolicyRepository bonusPolicyRepository;
    private final DiscountPolicyRepository discountPolicyRepository;
    private final CouponMapper couponMapper;
    private final CouponRepository couponRepository;
    private final MemberRepository memberRepository;
    private final ScheduleRepository scheduleRepository;
    private final ScheduleMapper scheduleMapper;
    private final TheaterMapper theaterMapper;

    /**
     * 상영관 전체 조회 (고객용)
     * @return 상영관 전체를 담은 리스트
     */
    @Override
    public List<TheaterDTO> getTheaterAll() {
        List<TheaterEntity> entityList = theaterRepository.findAll();
        return entityList.stream().map(TheaterEntity::toDTO).toList();
    }

    /**
     * 좌석 정책 전체 조회 (고객용)
     * @return 모든 좌석정책을 가져온 리스트
     */
    @Override
    public List<SeatPolicyDTO> readAllSeat() {
        List<SeatPolicyEntity> policy = seatPolicyRepository.findAll();
        return policy.stream().map(SeatPolicyEntity::toDTO).toList();
    }

    /**
     * 영화 단일 조회 (고객 상세 페이지용)
     * @param movieId 영화 PK
     * @return 지정 영화 DTO
     */
    @Override
    public MovieDTO getMovieById(Long movieId) {
        MovieEntity movieEntity = movieRepository.findById(movieId)
                .orElseThrow(() -> new NoSuchElementException("movieId를 찾을 수 없습니다"));
        return MovieEntity.toDTO(movieEntity);
    }

    /**
     * 해당날짜에 스케쥴이 있는 영화만 조회 (고객용)
     * @return 현재 상영중인 영화
     */
    @Override
    public List<MovieDTO> getScreeningPeriodAllMovies() {
        List<MovieEntity> movieEntityList = movieRepository.findAll();
        LocalDate now = LocalDate.now();

        List<MovieDTO> movieDTOList = new ArrayList<>();
        for (MovieEntity movieEntity : movieEntityList) {
            List<ScheduleEntity> byMovieEntityMovieId = scheduleRepository.findByMovieEntity_MovieId(movieEntity.getMovieId());
            for (ScheduleEntity scheduleEntity : byMovieEntityMovieId) {
                if (scheduleEntity.getStartAt().toLocalDate().isEqual(now) && scheduleEntity.isActivation()) {
                    movieDTOList.add(MovieEntity.toDTO(movieEntity));
                    break;
                }
            }
        }
        return movieDTOList;
    }

    /**
     * 적립정책 전체 조회 (고객용)
     * @return 전체 적립정책을 담은 리스트
     */
    @Override
    public List<BonusPolicyDTO> getBonusPolicies() {
        // 오늘 00:00:00 기준
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        List<BonusPolicyEntity> policyEntities = bonusPolicyRepository.findAllBonusPolicy(todayStart);
        return policyEntities.stream().map(BonusPolicyEntity::toDTO).toList();
    }

    /**
     * 쿠폰 사용 검증 (정책 기간, 사용여부, 정책에 해당하는 쿠폰, 쿠폰번호) (고객용)
     * @param couponNum 쿠폰번호
     * @return 사용 검증 통과시 쿠폰 DTO
     */
    @Override
    public CouponDTO authCoupon(String couponNum) {
        CouponVO couponVO = couponMapper.checkCoupon(couponNum);
        if (couponVO == null) {
            throw new NoSuchElementException("authCoupon... 해당하는 쿠폰 또는 정책이 없음");
        }
        if (!couponVO.getDiscountPolicy().isActivation()) {
            throw new IllegalStateException("authCoupon... 정책 비활성화");
        }
        LocalDateTime now = LocalDateTime.now();
        if (!(now.isBefore(couponVO.getDiscountPolicy().getEndAt()) && now.isAfter(couponVO.getDiscountPolicy().getStartAt()))) {
            throw new IllegalStateException("authCoupon...정책 만료");
        }
        CouponEntity coupon = couponRepository
                .findByCouponNumAndDiscountPolicyEntityIdAndStatusTrue(couponNum, couponVO.getDiscountPolicy().getId()).orElse(null);
        if (coupon == null) {
            throw new IllegalArgumentException("쿠폰 번호와 할인정책이 일치하고 사용여부가 true 인 녀셕 없음");
        }
        return CouponVO.toDTO(couponVO);
    }

    /**
     * 오늘 기준 활성화된 할인정책 전체 (내부 공용 — 권한 검증 없음)
     */
    private List<DiscountPolicyDTO> findActiveDiscountPolicies() {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        return discountPolicyRepository.findAllByEndAtAfter(todayStart).stream()
                .map(DiscountPolicyEntity::toDTO).toList();
    }

    /**
     * 연령 할인 정책 조회 (고객용)
     * @return AGE 타입 할인 정책 리스트
     */
    @Override
    public List<DiscountPolicyDTO> getAgeDiscounts() {
        List<DiscountPolicyDTO> agePolicy = findActiveDiscountPolicies().stream()
                .filter(p -> p.isActivation() && p.getConditionType() == ConditionType.AGE)
                .toList();
        if (agePolicy.isEmpty()) throw new IllegalStateException("연령 할인정책이 없거나 활성화 돼있지 않음 할인 X");
        return agePolicy;
    }

    /**
     * 시간 할인 정책 조회 (고객용)
     * @return TIME 타입 할인 정책 리스트
     */
    @Override
    public List<DiscountPolicyDTO> getTimeDiscounts() {
        List<DiscountPolicyDTO> timePolicy = findActiveDiscountPolicies().stream()
                .filter(p -> p.isActivation() && p.getConditionType() == ConditionType.TIME)
                .toList();
        if (timePolicy.isEmpty()) throw new IllegalStateException("조조 할인정책이 없거나 활성화 돼있지 않음 할인 X");
        return timePolicy;
    }

    /**
     * 신규 회원등록 (고객용)
     * @param memberDTO 회원 DTO
     */
    @Override
    public void createMember(MemberDTO memberDTO) {
        if (memberRepository.existsByPhone(memberDTO.getPhone())) {
            throw new IllegalStateException("회원 존재함 생성 불가능");
        }

        MemberDTO dto = MemberDTO.builder()
                .phone(memberDTO.getPhone())
                .grade(Grade.NORMAL)
                .point(memberDTO.getPoint())
                .createAt(LocalDateTime.now())
                .build();
        log.info("createMember... 신규 회원 정보 : {}", dto);
        memberRepository.save(MemberDTO.toEntity(dto));
    }

    /**
     * 회원 단일 조회 (고객용)
     * @param phone 회원 PK
     * @return 지정 회원 DTO
     */
    @Override
    public MemberDTO getMember(String phone) {
        MemberEntity entity = memberRepository.findById(phone).orElseThrow();
        return MemberEntity.toDTO(entity);
    }

    /**
     * 스케줄 전체 조회 (고객용)
     * @return 전체 스케줄 리스트
     */
    @Override
    public List<ScheduleDTO> getScheduleList() {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        List<ScheduleEntity> entityList = scheduleRepository.findAllByEndAtAfter(todayStart);
        return entityList.stream().map(ScheduleEntity::toDTO).toList();
    }

    /**
     * 스케줄 전체 조회 - Mapper 버전 (고객용)
     * @return 전체 스케줄 리스트
     */
    @Override
    public List<ScheduleDTO> getScheduleDTOList() {
        List<ScheduleVO> scheduleVOS = scheduleMapper.selectAll();
        List<ScheduleDTO> scheduleDTOS = new ArrayList<>();
        for (ScheduleVO scheduleVO : scheduleVOS) {
            scheduleDTOS.add(ScheduleVO.toDTO(scheduleVO));
        }
        return scheduleDTOS;
    }

    /**
     * 영화에 해당하는 스케줄 전체 조회 (고객용)
     * @param movieId 영화 PK
     * @return 해당하는 전체 영화 스케줄
     */
    @Override
    public List<ScheduleDTO> getScheduleListByMovieWithCustomer(Long movieId) {
        List<ScheduleEntity> entityList =
                scheduleRepository.findByMovieEntity_MovieIdAndStartAtAfterAndActivationTrue(movieId, LocalDateTime.now());
        return entityList.stream().map(ScheduleEntity::toDTO).toList();
    }

    /**
     * 상영관 단일 조회 (내부 전용 - 고객 결제 / 관리자 즉석예매 공용, 권한 검증 없음)
     * @param no 상영관 PK
     * @return 지정 상영관 DTO
     */
    @Override
    public TheaterDTO getTheater(Long no) {
        TheaterEntity entity = theaterRepository.findById(no).orElseThrow();
        return TheaterEntity.toDTO(entity);
    }

    /**
     * 좌석정책 단일 조회 (내부 전용 - 고객 결제 / 관리자 즉석예매 공용, 권한 검증 없음)
     * @param policyId 좌석정책 PK
     * @return 지정 좌석정책 DTO
     */
    @Override
    public SeatPolicyDTO readSeat(Long policyId) {
        SeatPolicyEntity policy = seatPolicyRepository.findById(policyId).orElseThrow();
        return SeatPolicyEntity.toDTO(policy);
    }

    /**
     * 쿠폰 단일 조회 (내부 전용 - 고객 결제 공용, 권한 검증 없음)
     * @param couponNum 쿠폰번호
     * @return 지정 쿠폰 DTO
     */
    @Override
    public CouponDTO getCoupon(String couponNum) {
        return CouponEntity.toDTO(couponRepository.findById(couponNum).orElseThrow());
    }

    /**
     * 적립정책 단일 조회 (내부 전용 - 고객 결제 공용, 권한 검증 없음)
     * @param grade 회원 등급
     * @return 지정 등급의 적립정책 DTO
     */
    @Override
    public BonusPolicyDTO getBonusPolicy(Grade grade) {
        if (grade == null) {
            throw new NoSuchElementException("지정 이름 적립정책 없음");
        }
        BonusPolicyEntity policy = bonusPolicyRepository.findByPolicyName(grade.name());
        return BonusPolicyEntity.toDTO(policy);
    }

    /**
     * 상영관 전체 조회 - Mapper 버전 (고객용, 권한 검증 없음)
     * @return 상영관 전체를 담은 리스트
     */
    @Override
    public List<TheaterDTO> getTheaterDTOAll() {
        List<TheaterVO> theaterVOS = theaterMapper.selectAll();
        List<TheaterDTO> theaterDTOS = new ArrayList<>();
        for (TheaterVO theaterVO : theaterVOS) {
            theaterDTOS.add(TheaterVO.toDTO(theaterVO));
        }
        return theaterDTOS;
    }
}
