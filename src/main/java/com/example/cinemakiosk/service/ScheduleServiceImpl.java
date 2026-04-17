package com.example.cinemakiosk.service;

import com.example.cinemakiosk.domain.MovieEntity;
import com.example.cinemakiosk.domain.ScheduleEntity;
import com.example.cinemakiosk.domain.TheaterEntity;
import com.example.cinemakiosk.dto.RequestDTO.ActivationRequest;
import com.example.cinemakiosk.dto.ScheduleDTO;
import com.example.cinemakiosk.mapper.ScheduleMapper;
import com.example.cinemakiosk.repository.MovieRepository;
import com.example.cinemakiosk.repository.ScheduleRepository;
import com.example.cinemakiosk.repository.TheaterRepository;
import com.example.cinemakiosk.vo.ScheduleVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    public ScheduleDTO createSchedule(ScheduleDTO scheduleDTO) {

        // 영화조회와 런타임을 가져오기 위함
        MovieEntity movieEntity = movieRepository.findById(scheduleDTO.getMovieId()).orElseThrow();
        // 상영관 조회를 위함
        TheaterEntity theaterEntity = theaterRepository.findById(scheduleDTO.getNo()).orElseThrow();
        // 상영종료 시간
        LocalDateTime endAt = scheduleDTO.getStartAt().plusMinutes(movieEntity.getRuntime());

        if (scheduleMapper.checkScheduleOverlap(scheduleDTO.getNo(), scheduleDTO.getStartAt(), endAt) > 0) {
            throw new IllegalStateException("지정된 상영관에 시간이 겹칩니다. 등록 실패");
        }

        ScheduleDTO dto = ScheduleDTO.builder()
                .startAt(scheduleDTO.getStartAt())
                .endAt(endAt.plusMinutes(theaterEntity.getCleanupTime()))
                .no(scheduleDTO.getNo())
                .movieId(scheduleDTO.getMovieId())
                .activation(true)
                .build();
        log.info("createSchedule... 상영관 정리시간(분) : {}", theaterEntity.getCleanupTime());

        ScheduleEntity entity = scheduleRepository.save(ScheduleDTO.toEntity(dto));
        log.info("createSchedule... 스케줄 등록 목록: {}, 좌석 정책 번호: {}, 영화 번호: {}", entity,
                entity.getTheaterEntity().getNo(),
                entity.getMovieEntity().getMovieId());

        return ScheduleDTO.builder()
                .id(entity.getId())
                .startAt(scheduleDTO.getStartAt())
                .endAt(endAt.plusMinutes(theaterEntity.getCleanupTime()))
                .no(scheduleDTO.getNo())
                .movieId(scheduleDTO.getMovieId())
                .activation(false)
                .build();
    }

    /**
     * 스케줄 수정 메서드
     * @param scheduleDTO 스케줄 DTO
     */
    @Override
    public void updateSchedule(ScheduleDTO scheduleDTO) {
        // 수정할 스케줄 선택
        ScheduleEntity scheduleEntity = scheduleRepository.findById(scheduleDTO.getId()).orElseThrow();
        // 영화와 런타임을 가져오기 위함
        MovieEntity movieEntity = movieRepository.findById(scheduleDTO.getMovieId()).orElseThrow();
        // 상영관과 정리시간을 가져오기 위함
        TheaterEntity theaterEntity = theaterRepository.findById(scheduleDTO.getNo()).orElseThrow();
        // 상영종료 시간
        LocalDateTime endAt = scheduleDTO.getStartAt().plusMinutes(movieEntity.getRuntime());

        // 이미 지난 스케줄(상영 시작시간이 현재 시간보다 이전)은 수정 불가
        if (LocalDateTime.now().isAfter(scheduleEntity.getEndAt())) {
            log.info("수정 요청 : {} ", scheduleDTO);
            throw new IllegalStateException("이미 지난 스케줄 수정 실패");
        }

        /*
        수정할때 아직 수정전이라 자기자신도 그대로 남아있기때문에 자기자신은 제외 시키고 시간이 겹치는지 검증해야함
         */

        // 수정 후 시간이 같은 상영관의 다른 스케줄과 겹치는지 검증 (자기자신 제외)
        if (scheduleMapper.checkScheduleOverlapExcludeSelf(scheduleDTO.getNo(), scheduleDTO.getStartAt(),
                endAt, scheduleEntity.getId()) > 0) {
            log.info("수정 요청 : {} ", scheduleDTO);
            throw new IllegalStateException("지정 상영관의 시간과 수정을 요청한 시간과 겹칩니다 수정 실패");
        }


        // 수정 시 endAt도 런타임 + 정리시간 기준으로 재계산
        scheduleEntity.changeStartAt(scheduleDTO.getStartAt(), endAt.plusMinutes(theaterEntity.getCleanupTime()));
        // 상영관 변경
        scheduleEntity.changeTheater(theaterEntity);
        // 영화 변경
        scheduleEntity.changeMovie(movieEntity);
        log.info("updateSchedule... 수정완료 {}", scheduleEntity);
        scheduleRepository.save(scheduleEntity);
    }

    /**
     * 스케줄 상태 변경
     * @param request 요청 DTO
     */
    @Override
    public void updateActivation(ActivationRequest request) {
        List<ScheduleEntity> scheduleEntities = scheduleRepository.findAllById(request.getIds());

        scheduleEntities.forEach(scheduleEntity -> {
            // 만약 이미 지나간 스케줄이라면 상태 변경 불가
            if (LocalDateTime.now().isAfter(scheduleEntity.getEndAt())) {
                log.error("updateActivation... 이미 지나간 스케줄 변경 실패");
                return;
            }
            if (scheduleEntity.isActivation() == request.isActivation()) {
                log.error("만료여부 일치 변경 실패 : 현재 {}, 요청 {} ", scheduleEntity, request);
                return;
            }
            scheduleEntity.changeActivation(request.isActivation());
            log.info("updateActivation... 스케줄 상태 변경 성공 : {}", scheduleEntities);
        });

        scheduleRepository.saveAll(scheduleEntities);

    }

    /**
     * 스케줄 제거 메서드
     * @param ids 스케줄 PKs
     */
    @Override
    public void deleteSchedule(List<Long> ids) {
        List<ScheduleEntity> scheduleEntity = scheduleRepository.findAllById(ids);
        scheduleEntity.forEach(schedule -> {
            scheduleRepository.deleteById(schedule.getId());
        });
    }

    /**
     * 스케줄 전체 조회
     * @return 전체 스케줄 리스트
     */
    @Override
    public List<ScheduleDTO> getScheduleList() {
        List<ScheduleEntity> entityList = scheduleRepository.findAllByEndAtAfter(LocalDateTime.now());
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
     * @param id 스케줄 PK
     * @return 스케줄 단일
     */
    @Override
    public ScheduleDTO getSchedule(Long id) {
        ScheduleEntity schedule = scheduleRepository.findById(id).orElseThrow();
        return ScheduleEntity.toDTO(schedule);
    }


    @Override
    public List<ScheduleDTO> getScheduleDTOList() {
        List<ScheduleVO> scheduleVOS = scheduleMapper.selectAll();
        List<ScheduleDTO> scheduleDTOS = new ArrayList<>();

        for (ScheduleVO scheduleVO : scheduleVOS){
            scheduleDTOS.add(ScheduleVO.toDTO(scheduleVO));
        }
        log.info("반환받은 스케쥴 정보 : {}",scheduleDTOS);

        return scheduleDTOS;
    }

    @Override
    public ScheduleDTO getScheduleDTO(Long no) {
        ScheduleVO scheduleVO = scheduleMapper.selectOneById(no);
        return ScheduleVO.toDTO(scheduleVO);
    }
}
