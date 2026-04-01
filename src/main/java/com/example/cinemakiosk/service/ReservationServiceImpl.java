package com.example.cinemakiosk.service;

import com.example.cinemakiosk.domain.ReservationDetailsEntity;
import com.example.cinemakiosk.dto.ReservationDetailsDTO;
import com.example.cinemakiosk.dto.ReservationSeatDTO;
import com.example.cinemakiosk.mapper.ReservationDetailsMapper;
import com.example.cinemakiosk.mapper.ReservationSeatMapper;
import com.example.cinemakiosk.repository.ReservationDetailsRepository;
import com.example.cinemakiosk.repository.ReservationSeatRepository;
import com.example.cinemakiosk.vo.ReservationDetailsVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {
    private final ReservationSeatMapper reservationSeatMapper;
    private final ReservationSeatRepository reservationSeatRepository;
    private final ReservationDetailsMapper reservationDetailsMapper;
    private final ReservationDetailsRepository reservationDetailsRepository;

    //예매 진행
    @Override
    public void create(ReservationDetailsDTO reservationDetailsDTO){
        //seat와 예매에 대한 부분을 나눠서 repository와 mapper로 각각 등록.
        List<ReservationSeatDTO> seats = reservationDetailsDTO.getSeats();

        if (seats.isEmpty()){
            log.info("좌석 정보가 없어서 등록 예외 발생.");
            return;
        }

        log.info("reservationDetails에 대한 부분을 등록");
        ReservationDetailsEntity reservationDetailsEntity = ReservationDetailsDTO.toEntity(reservationDetailsDTO);
        ReservationDetailsEntity result = reservationDetailsRepository.save(reservationDetailsEntity);

        log.info("좌석 정보를 등록");
        reservationDetailsDTO.setId(result.getId());
        ReservationDetailsVO reservationDetailsVO = ReservationDetailsDTO.toVO(reservationDetailsDTO);
        reservationSeatMapper.insertSeats(reservationDetailsVO);

        log.info("예매정보 등록 완료");
    }

    //예매 내역 조회
    @Override
    public ReservationDetailsDTO read(Long no){
        ReservationDetailsVO reservationDetailsVO = reservationDetailsMapper.selectOneById(no);
        log.info("아니 이거 뭐야 먼저 말좀 : {} ",reservationDetailsVO);
        return ReservationDetailsVO.toDTO(reservationDetailsVO);

    }

    //예매 내역 전체 조회
    @Override
    public List<ReservationDetailsDTO> readAll(){
        return List.of();
    }

    @Override
    public List<String> readAllSeatByScheduleId(Long scheduleId) {
        return List.of();
    }

    //예매 내역 변경.
    @Override
    public void update(ReservationDetailsDTO reservationDetailsDTO){

    }

    //유효기간 지난 예매 삭제
    @Override
    public void delete(Long no){

    }
}
