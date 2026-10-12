package com.example.cinemakiosk.service;

import com.example.cinemakiosk.domain.ReservationDetailsEntity;
import com.example.cinemakiosk.dto.ReservationDetailsDTO;
import com.example.cinemakiosk.dto.ReservationSeatDTO;
import com.example.cinemakiosk.mapper.ReservationDetailsMapper;
import com.example.cinemakiosk.mapper.ReservationSeatMapper;
import com.example.cinemakiosk.repository.ReservationDetailsRepository;
import com.example.cinemakiosk.vo.ReservationDetailsVO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Log4j2
@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {
    private final ReservationSeatMapper reservationSeatMapper;
    private final ReservationDetailsMapper reservationDetailsMapper;
    private final ReservationDetailsRepository reservationDetailsRepository;

    //예매 진행
    @Transactional
    @Override
    public ReservationDetailsDTO create(ReservationDetailsDTO reservationDetailsDTO){
        // 컨트롤러에 직접 노출되지 않는 내부 전용 메서드 (고객 결제 흐름 / 관리자 즉석예매 양쪽에서 호출) - 권한 검증 없음
        //seat와 예매에 대한 부분을 나눠서 repository와 mapper로 각각 등록.
        List<ReservationSeatDTO> seats = reservationDetailsDTO.getSeats();

        if (seats.isEmpty()){
            throw new NoSuchElementException("좌석 정보가 없어서 등록 예외 발생.");
        }

        log.info("reservationDetails에 대한 부분을 등록");
        ReservationDetailsEntity reservationDetailsEntity = ReservationDetailsDTO.toEntity(reservationDetailsDTO);
        ReservationDetailsEntity result = reservationDetailsRepository.saveAndFlush(reservationDetailsEntity);

        log.info("좌석 정보를 등록");
        reservationDetailsDTO.setId(result.getId());
        ReservationDetailsVO reservationDetailsVO = ReservationDetailsDTO.toVO(reservationDetailsDTO);
        reservationSeatMapper.insertSeats(reservationDetailsVO);

        log.info("예매정보 등록 완료");
        return reservationDetailsDTO;
    }

    @Override
    public List<String> readAllReservationSeatByScheduleId(Long scheduleId) {
        return reservationSeatMapper.selectAllSeatByScheduleId(scheduleId);
    }

    @Override
    public List<ReservationDetailsDTO> readSeatByMovieId(Long movieId) {
        List<ReservationDetailsVO> reservationDetailsVOS = reservationDetailsMapper.selectAllByMovieId(movieId);
        log.info("받은 데이터 : {}", reservationDetailsVOS);
        List<ReservationDetailsDTO> reservationDetailsDTOS = new ArrayList<>();

        for (ReservationDetailsVO reservationVo : reservationDetailsVOS){
            reservationDetailsDTOS.add(ReservationDetailsVO.toDTO(reservationVo));
        }

        return reservationDetailsDTOS;
    }
}
