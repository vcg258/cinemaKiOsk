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
    public void create(ReservationDetailsDTO reservationDetailsDTO){
        //seat와 예매에 대한 부분을 나눠서 repository와 mapper로 각각 등록.
        List<ReservationSeatDTO> seats = reservationDetailsDTO.getSeats();

        if (seats.isEmpty()){
            log.info("좌석 정보가 없어서 등록 예외 발생.");
            return;
        }

        log.info("reservationDetails에 대한 부분을 등록");
        ReservationDetailsEntity reservationDetailsEntity = ReservationDetailsDTO.toEntity(reservationDetailsDTO);
        ReservationDetailsEntity result = reservationDetailsRepository.saveAndFlush(reservationDetailsEntity);

        log.info("좌석 정보를 등록");
        reservationDetailsDTO.setId(result.getId());
        ReservationDetailsVO reservationDetailsVO = ReservationDetailsDTO.toVO(reservationDetailsDTO);
        reservationSeatMapper.insertSeats(reservationDetailsVO);

        log.info("예매정보 등록 완료");
    }

    //예매 내역 조회
    @Override
    public ReservationDetailsDTO read(String no){
        ReservationDetailsVO reservationDetailsVO = reservationDetailsMapper.selectOneById(no);
        log.info("조회한 예매 내역 : {} ",reservationDetailsVO);
        return ReservationDetailsVO.toDTO(reservationDetailsVO);
    }

    //예매 내역 전체 조회
    @Override
    public List<ReservationDetailsDTO> readAll(){
        List<ReservationDetailsVO> reservationDetailsVOS = reservationDetailsMapper.selectAll();
        List<ReservationDetailsDTO> reservationDetailsDTOS = new ArrayList<>();

        for (ReservationDetailsVO reservationVo : reservationDetailsVOS){
            reservationDetailsDTOS.add(ReservationDetailsVO.toDTO(reservationVo));
        }

        return reservationDetailsDTOS;
    }

    @Override
    public List<String> readAllReservationSeatByScheduleId(Long scheduleId) {
        return reservationSeatMapper.selectAllSeatByScheduleId(scheduleId);
    }

    //예매 내역 변경.
    @Override
    public void update(ReservationDetailsDTO reservationDetailsDTO){
        ReservationDetailsEntity reservationDetailsEntity = ReservationDetailsDTO.toEntity(reservationDetailsDTO);
        reservationDetailsRepository.save(reservationDetailsEntity);
    }

    //환불의 경우 예매 좌석 삭제 처리로 자리 잡을 수 있게 해줌.
    //삭제하면 안되고 캔슬됨으로 변경해야함. returned를 true로 변경
    @Override
    public void returned(ReservationDetailsDTO reservationDetailsDTO){
        reservationDetailsRepository.save(ReservationDetailsDTO.toEntity(reservationDetailsDTO));
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
