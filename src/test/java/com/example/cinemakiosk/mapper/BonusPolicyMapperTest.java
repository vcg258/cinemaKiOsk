package com.example.cinemakiosk.mapper;

import com.example.cinemakiosk.vo.BonusPolicyVO;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@Log4j2
@SpringBootTest
class BonusPolicyMapperTest {
    @Autowired private BonusPolicyMapper bonusPolicyMapper;

    @Test
    public void selectTest(){
        BonusPolicyVO bonusPolicyVO = bonusPolicyMapper.selectOneById(1L);
        log.info(bonusPolicyVO);
    }

}