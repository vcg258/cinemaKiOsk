package com.example.cinemakiosk.service;

import com.example.cinemakiosk.domain.MovieEntity;
import com.example.cinemakiosk.domain.ScheduleEntity;
import com.example.cinemakiosk.domain.TheaterEntity;
import com.example.cinemakiosk.dto.ScheduleDTO;
import com.example.cinemakiosk.mapper.ScheduleMapper;
import com.example.cinemakiosk.repository.MovieRepository;
import com.example.cinemakiosk.repository.ScheduleRepository;
import com.example.cinemakiosk.repository.TheaterRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
    public void createSchedule(ScheduleDTO scheduleDTO) {

        // 영화조회와 런타임을 가져오기 위함
        MovieEntity movieEntity = movieRepository.findById(scheduleDTO.getMovieId()).orElseThrow();
        // 상영관 조회를 위함
        TheaterEntity theaterEntity = theaterRepository.findById(scheduleDTO.getNo()).orElseThrow();
        // 상영종료 시간
        LocalDateTime endAt = scheduleDTO.getStartAt().plusMinutes(movieEntity.getRuntime());

        if (scheduleMapper.checkScheduleOverlap(scheduleDTO.getNo(), scheduleDTO.getStartAt(), endAt) > 0) {
            log.info("createSchedule... 지정된 상영관에 시간이 겹칩니다. 등록 실패");
            return;
        }

        ScheduleDTO dto = ScheduleDTO.builder()
                .startAt(scheduleDTO.getStartAt())
                .endAt(endAt.plusMinutes(theaterEntity.getCleanupTime()))
                .no(scheduleDTO.getNo())
                .movieId(scheduleDTO.getMovieId())
                .build();

        log.info("createSchedule... 상영관 정리시간(분) : {}", theaterEntity.getCleanupTime());

        ScheduleEntity entity = scheduleRepository.save(ScheduleDTO.toEntity(dto));
        log.info("createSchedule... 스케줄 등록 목록: {}, 좌석 정책 번호: {}, 영화 번호: {}", entity,
                entity.getTheaterEntity().getNo(),
                entity.getMovieEntity().getMovieId());
    }

    /**
     * 스케줄 수정 메서드
     * @param scheduleDTO 스케줄 DTO
     */
    @Override
    public void updateSchedule(ScheduleDTO scheduleDTO) {
        // TODO 제약조건 1: 이미 지난 스케줄(start_at이 현재 시간보다 이전)은 수정 불가
        // TODO 제약조건 2: 수정 후 시간이 같은 상영관의 다른 스케줄과 겹치는지 검증 (자기 자신 제외하고 체크)
        // TODO 제약조건 3: 수정 시 endAt도 런타임 + 정리시간 기준으로 재계산
    }

    /**
     * 스케줄 제거 메서드
     * @param scheduleId 스케줄 PK
     */
    @Override
    public void deleteSchedule(Long scheduleId) {
        // TODO 제약조건 1: 이미 상영이 시작된 스케줄(start_at이 현재 시간보다 이전)은 삭제 불가
        // TODO 제약조건 2: 해당 스케줄에 예약된 좌석이 있으면 삭제 불가 (예약 파트 구현 후 연동 필요)
    }

    /**
     * 스케줄 전체 조회
     * @return 전체 스케줄 리스트
     */
    @Override
    public List<ScheduleDTO> getScheduleList() {
        List<ScheduleEntity> entityList = scheduleRepository.findAll();
        return entityList.stream().map(ScheduleEntity::toDTO).toList();
    }

    /**
     * 영화에 해당하는 스케줄 전체 조회
     * @param movieId 영화 PK
     * @return 해당하는 전체 영화 리스트
     */
    @Override
    public List<ScheduleDTO> getScheduleListByMovie(Long movieId) {
        List<ScheduleEntity> entityList = scheduleRepository.findByMovieEntity_MovieId(movieId);
        return entityList.stream().map(ScheduleEntity::toDTO).toList();
    }

    /**
     * 스케줄 단일 조회
     * @param scheduleId 스케줄 PK
     * @return 스케줄 단일
     */
    @Override
    public ScheduleDTO getSchedule(Long scheduleId) {
        ScheduleEntity schedule = scheduleRepository.findById(scheduleId).orElseThrow();
        return ScheduleEntity.toDTO(schedule);
    }
}
