package com.example.cinemakiosk.mapper;

import com.example.cinemakiosk.vo.TheaterVO;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Log4j2
@SpringBootTest
class TheaterMapperTest {
    @Autowired private TheaterMapper theaterMapper;

    @Test
    public void selectOne(){
        TheaterVO theaterVO = theaterMapper.selectOneById(1L);
        log.info(theaterVO);
    }

    @Test
    public void selectAll(){
        List<TheaterVO> theaterVOS = theaterMapper.selectAll();
        log.info(theaterVOS);
    }

}