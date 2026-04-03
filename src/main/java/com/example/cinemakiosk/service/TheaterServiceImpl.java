package com.example.cinemakiosk.service;

import com.example.cinemakiosk.domain.SeatPolicyEntity;
import com.example.cinemakiosk.domain.TheaterEntity;
import com.example.cinemakiosk.dto.SeatPolicyDTO;
import com.example.cinemakiosk.dto.TheaterDTO;
import com.example.cinemakiosk.mapper.SeatPolicyMapper;
import com.example.cinemakiosk.mapper.TheaterMapper;
import com.example.cinemakiosk.repository.SeatPolicyRepository;
import com.example.cinemakiosk.repository.TheaterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Log4j2
@Service
@RequiredArgsConstructor
public class TheaterServiceImpl implements TheaterService {
    private final SeatPolicyRepository seatPolicyRepository;
    private final SeatPolicyMapper seatPolicyMapper;
    private final TheaterRepository theaterRepository;
    private final TheaterMapper theaterMapper;

    /**
     * 상영관 등록
     * @param theaterDTO 상영관 DTO
     */
    @Override
    public void createTheater(TheaterDTO theaterDTO) {
        TheaterDTO dto = TheaterDTO.builder()
                .policyId(theaterDTO.getPolicyId())
                .cleanupTime(theaterDTO.getCleanupTime())
                .build();

        theaterRepository.save(TheaterDTO.toEntity(dto));
        log.info("createTheater... 등록완료 {}", dto);
    }

    /**
     * 상영관 전체 조회
     * @return 상영관 전체를 담은 리스트
     */
    @Override
    public List<TheaterDTO> getTheaterAll() {
        List<TheaterEntity> entityList = theaterRepository.findAll();
        return entityList.stream().map(TheaterEntity::toDTO).toList();
    }

    /**
     * 상영관 단일 조회
     * @return 단일 상영관 조회
     */
    @Override
    public TheaterDTO getTheater(Long no) {
        TheaterEntity entity = theaterRepository.findById(no).orElseThrow();
        return TheaterEntity.toDTO(entity);
    }

    /**
     * 상영관 좌석정책 변경
     * @param theaterDTO 상영관 DTO
     */
    @Override
    public void updateSeatPolicy(TheaterDTO theaterDTO) {
        TheaterEntity theater = theaterRepository.findById(theaterDTO.getNo()).orElseThrow();
        SeatPolicyEntity seatPolicy = seatPolicyRepository.findById(theaterDTO.getPolicyId()).orElseThrow();
        theater.changeSeatPolicy(seatPolicy);
        theaterRepository.save(theater);
        log.info("updateSeatPolicy... 좌석 정책 업데이트 {} 변경된 정책 {}", theater, seatPolicy);
    }

    /**
     * 상영관 정리식간 변경
     * @param theaterDTO 상영관 DTO
     */
    @Override
    public void updateCleanTime(TheaterDTO theaterDTO) {
        TheaterEntity theater = theaterRepository.findById(theaterDTO.getNo()).orElseThrow();
        theater.changeCleantime(theaterDTO.getCleanupTime());
        theaterRepository.save(theater);
        log.info("updateCleanTime... 정리시간 업데이트 {}", theater);
    }

    /**
     * 좌석 정책 생성 / 추가 (일반: 5000, 리클라이너: 10000, 커플석: 15000, VIP: 7000)
     * @param seatPolicyDTO 좌석정책 DTO
     */
    @Override
    public void createSeat(SeatPolicyDTO seatPolicyDTO) {
        SeatPolicyEntity dto = SeatPolicyEntity.builder()
                .name(seatPolicyDTO.getName())
                .cost(seatPolicyDTO.getCost())
                .build();

        // 중복 이름 예외
        if (seatPolicyRepository.existsByName(dto.getName())) {
            log.error("중복된 좌석정책이 있습니다.");
            return;
        }

        SeatPolicyEntity seatPolicyEntity = seatPolicyRepository.save(dto);
        log.info("createSeat... 좌석 정책 생성 / 추가 내역: {}", seatPolicyEntity);
    }

    /**
     * 좌석 정책 전체를 확인
     * @return 모든 좌석정책을 가져온 리스트
     */
    @Override
    public List<SeatPolicyDTO> readAllSeat() {
        List<SeatPolicyEntity> policy = seatPolicyRepository.findAll();

//        List<SeatPolicyDTO> dtos = new ArrayList<>();
//
//        for (SeatPolicyEntity dto : policy) {
//            dtos.add(SeatPolicyEntity.toDTO(dto));
//        }
//
//        return dtos;


//        return policy.stream().map(item ->
//            SeatPolicyDTO.builder()
//                    .policyId(item.getPolicyId())
//                    .name(item.getName())
//                    .cost(item.getCost())
//                    .build()).toList();

        return policy.stream().map(SeatPolicyEntity::toDTO).toList();
    }

    /**
     * 좌석 정책 1개만 조회
     * @param policyId 좌석정책 PK
     * @return 지정 좌석 정책
     */
    @Override
    public SeatPolicyDTO readSeat(Long policyId) {
        SeatPolicyEntity policy = seatPolicyRepository.findById(policyId).orElseThrow();
        return SeatPolicyEntity.toDTO(policy);
    }

    /**
     * 좌석 정책 수정
     * @param seatPolicyDTO 좌석정책 DTO
     */
    @Override
    public void updateSeat(SeatPolicyDTO seatPolicyDTO) {
        SeatPolicyEntity policy = seatPolicyRepository.findById(seatPolicyDTO.getPolicyId()).orElseThrow();
        policy.updateSeatPolicy(seatPolicyDTO.getName(), seatPolicyDTO.getCost());

        SeatPolicyEntity seatPolicy = seatPolicyRepository.save(policy);
        log.info("updateSeat... 수정사항 적용 : {}" , seatPolicy);
    }

    /**
     * 좌석 정책 단일 삭제
     * @param policyId 좌석 정책 PK
     */
    @Override
    public void deleteSeat(Long policyId) {
        if (theaterRepository.existsBySeatPolicyEntity_PolicyId(policyId)) {
            log.error("deleteSeat... 현재 사용중인 좌석정책 삭제 불가");
            return;
        }
        seatPolicyRepository.deleteById(policyId);
    }
}
