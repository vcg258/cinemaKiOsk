package com.example.cinemakiosk.mapper;

import com.example.cinemakiosk.vo.ReservationDetailsVO;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Log4j2
@SpringBootTest
class ReservationDetailsMapperTest {
    @Autowired
    private ReservationDetailsMapper reservationDetailsMapper;

    @Test
    public void selectOne(){
        ReservationDetailsVO reservationDetailsVO = reservationDetailsMapper.selectOneById("1");
        log.info(reservationDetailsVO);
    }
    @Test
    public void selectAll(){
        List<ReservationDetailsVO> reservationDetailsVOS = reservationDetailsMapper.selectAll();
        log.info(reservationDetailsVOS);
    }

}