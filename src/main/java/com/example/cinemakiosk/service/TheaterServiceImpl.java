package com.example.cinemakiosk.service;

import com.example.cinemakiosk.domain.SeatPolicyEntity;
import com.example.cinemakiosk.dto.SeatPolicyDTO;
import com.example.cinemakiosk.dto.TheaterDTO;
import com.example.cinemakiosk.mapper.SeatPolicyMapper;
import com.example.cinemakiosk.mapper.TheaterMapper;
import com.example.cinemakiosk.repository.SeatPolicyRepository;
import com.example.cinemakiosk.repository.TheaterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

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
     * 좌석 정책 생성 / 추가 (일반: 5000, 리클라이너: 10000, 커플석: 15000, VIP: 7000)
     * @param seatPolicyDTO 좌석 정책 DTO
     */
    @Override
    public void createSeat(SeatPolicyDTO seatPolicyDTO) {
        SeatPolicyEntity dto = SeatPolicyEntity.builder()
                .name(seatPolicyDTO.getName())
                .cost(seatPolicyDTO.getCost())
                .build();
        SeatPolicyEntity seatPolicyEntity = seatPolicyRepository.save(dto);
        log.info("createSeat... 좌석 정책 생성 / 추가 내역: {}", seatPolicyEntity);
    }

    /**
     * 좌석 정책 1개만 조회
     * @param no 상영관 PK
     * @return 지정 좌석 정책
     */
    @Override
    public TheaterDTO readSeat(Long no) {

        return null;
    }

    /**
     * @return
     */
    @Override
    public List<TheaterDTO> readAllSeat() {
        return List.of();
    }

    /**
     * @param seatPolicyDTO
     */
    @Override
    public void updateSeat(SeatPolicyDTO seatPolicyDTO) {

    }

    /**
     * @param no
     */
    @Override
    public void deleteSeat(Long no) {

    }
}
