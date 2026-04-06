package com.example.cinemakiosk.mapper;

import com.example.cinemakiosk.vo.ReservationSeatVO;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Log4j2
@SpringBootTest
class ReservationSeatMapperTest {
    @Autowired
    private ReservationSeatMapper reservationSeatMapper;

    @Test
    public void selectAll(){
        List<ReservationSeatVO> reservationSeatVOS = reservationSeatMapper.selectAllById(1L);
        log.info(reservationSeatVOS);
    }

}