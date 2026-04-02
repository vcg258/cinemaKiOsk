package com.example.cinemakiosk.mapper;

import com.example.cinemakiosk.vo.SeatPolicyVO;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Log4j2
@SpringBootTest
class SeatPolicyMapperTest {
    @Autowired private SeatPolicyMapper seatPolicyMapper;

    @Test
    public void selectOne(){
        SeatPolicyVO seatPolicyVO = seatPolicyMapper.selectOneById(1L);
        log.info(seatPolicyVO);
    }

    @Test
    public void selectAll(){
        List<SeatPolicyVO> seatPolicyVOS = seatPolicyMapper.selectAll();
        log.info(seatPolicyVOS);
    }

}