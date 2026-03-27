package com.example.cinemakiosk.service;

import com.example.cinemakiosk.dto.ScheduleDTO;
import com.example.cinemakiosk.mapper.ScheduleMapper;
import com.example.cinemakiosk.repository.ScheduleRepository;
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

    /**
     * 스케줄 등록 메서드
     * @param scheduleDTO 스케줄 DTO
     */
    @Override
    public void createSchedule(ScheduleDTO scheduleDTO) {
        
    }

    /**
     * @param scheduleDTO
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
