package com.example.cinemakiosk.service;

import com.example.cinemakiosk.domain.MovieEntity;
import com.example.cinemakiosk.domain.ScheduleEntity;
import com.example.cinemakiosk.domain.TheaterEntity;
import com.example.cinemakiosk.dto.ScheduleDTO;
import com.example.cinemakiosk.mapper.ScheduleMapper;
import com.example.cinemakiosk.repository.MovieRepository;
import com.example.cinemakiosk.repository.ScheduleRepository;
import com.example.cinemakiosk.repository.TheaterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final ScheduleMapper scheduleMapper;
    private final MovieRepository movieRepository;
    private final TheaterRepository theaterRepository;

    /**
     * 스케줄 등록 메서드
     * @param scheduleDTO 스케줄 DTO
     */
    @Override
    public void createSchedule(ScheduleDTO scheduleDTO) { // TODO 제약 조건이 필요해보임 뭔가 허전함

        // 영화조회와 런타임을 가져오기 위함
        MovieEntity movieEntity = movieRepository.findById(scheduleDTO.getMovieId()).orElseThrow();
        // 상영관 조회를 위함
        TheaterEntity theaterEntity = theaterRepository.findById(scheduleDTO.getNo()).orElseThrow();

        if (movieEntity.getMovieId() == null || theaterEntity.getNo() == null) {
            log.error("createSchedule... 지정한 영화나 좌석정책이 없습니다. 등록 실패");
            return;
        }
        if (scheduleMapper.checkScheduleOverlap(scheduleDTO.getNo(), scheduleDTO.getStartAt(), scheduleDTO.getEndAt()) > 1) {
            log.info("createSchedule... 지정된 상영관에 시간이 겹칩니다. 등록 실패");
            return;
        }

        ScheduleDTO dto = ScheduleDTO.builder()
                .startAt(scheduleDTO.getStartAt())
                .endAt(scheduleDTO.getStartAt().plusMinutes(movieEntity.getRuntime())) // 시작시간에서 자동 종료시간 계산
                .no(scheduleDTO.getNo())
                .movieId(scheduleDTO.getMovieId())
                .build();

        ScheduleEntity entity = scheduleRepository.save(ScheduleDTO.toEntity(dto));
        log.info("createSchedule... 스케줄 등록 목록: {}, 좌석 정책 번호: {}, 영화 번호: {}", entity,
                entity.getTheaterEntity().getNo(),
                entity.getMovieEntity().getMovieId());
    }

    /**
     * 스케줄 수정
     * @param scheduleDTO 스케줄 DTO
     */
    @Override
    public void updateSchedule(ScheduleDTO scheduleDTO) {

    }

    /**
     * @param scheduleId
     */
    @Override
    public void deleteSchedule(Long scheduleId) {

    }

    /**
     * @return
     */
    @Override
    public List<ScheduleDTO> getScheduleList() {
        return List.of();
    }

    /**
     * @param movieId
     * @return
     */
    @Override
    public List<ScheduleDTO> getScheduleListByMovie(Long movieId) {
        return List.of();
    }

    /**
     * @param scheduleId
     * @return
     */
    @Override
    public ScheduleDTO getSchedule(Long scheduleId) {
        return null;
    }
}
