package com.example.cinemakiosk.mapper;

import com.example.cinemakiosk.vo.ScheduleVO;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Log4j2
@SpringBootTest
class ScheduleMapperTest {
    @Autowired private ScheduleMapper scheduleMapper;

    @Test
    public void selectOne(){
        ScheduleVO scheduleVO = scheduleMapper.selectOneById(1L);
        log.info(scheduleVO);
    }

    @Test
    public void selectAll(){
        List<ScheduleVO> scheduleVOS = scheduleMapper.selectAll();
        log.info(scheduleVOS);
    }


}