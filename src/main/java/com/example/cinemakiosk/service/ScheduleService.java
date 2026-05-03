package com.example.cinemakiosk.service;

import com.example.cinemakiosk.dto.requestDTO.ActivationRequest;
import com.example.cinemakiosk.dto.ScheduleDTO;

import java.util.List;

public interface ScheduleService {
    // 스케줄 등록
    void createSchedule(ScheduleDTO scheduleDTO);

    // 스케줄 수정
    void updateSchedule(ScheduleDTO scheduleDTO);

    // 스케줄 상태 변경
    void updateActivation(ActivationRequest request);

    // 스케줄 삭제
    void deleteSchedule(List<Long> ids);

    // 스케줄 전체 목록 조회
    List<ScheduleDTO> getScheduleList();

    // 영화에 해당하는 스케줄 목록 전체 조회
    List<ScheduleDTO> getScheduleListByMovie(Long movieId);

    // 영화에 해당하는 스케줄 목록 전체 조회 고객용 (현재 시간기준 지나간것들은 조회 안함)
    List<ScheduleDTO> getScheduleListByMovieWithCustomer(Long movieId);

    // 스케줄 하나 조회
    ScheduleDTO getSchedule(Long id);

    // Mapper 전체 조회
    List<ScheduleDTO> getScheduleDTOList();

    // Mapper 단일 조회
    ScheduleDTO getScheduleDTO(Long no);
}
