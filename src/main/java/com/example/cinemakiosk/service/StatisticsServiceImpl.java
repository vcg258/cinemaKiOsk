package com.example.cinemakiosk.service;

import com.example.cinemakiosk.dto.StatisticsDTO;
import com.example.cinemakiosk.mapper.StatisticsMapper;
import com.example.cinemakiosk.vo.StatisticsVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {
    private final StatisticsMapper statisticsMapper;

    /**
     * 년, 월, 일 기간 설정한 일별 데이터 조회 (계산)
     * @param startDate 시작일
     * @param endDate 마지막일
     * @param type 년, 월, 일
     * @return 기간 통계 데이터
     */
    @Override
    public List<StatisticsDTO> getStatistics(LocalDate startDate, LocalDate endDate, String type) {
        List<StatisticsVO> statistics = statisticsMapper.getStatistics(startDate, endDate, type);
        return statistics.stream().map(StatisticsVO::toDTO).toList();
    }
}
