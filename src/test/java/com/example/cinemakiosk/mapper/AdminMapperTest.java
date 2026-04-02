package com.example.cinemakiosk.mapper;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Log4j2
@SpringBootTest
class AdminMapperTest {
    @Autowired
    AdminMapper adminMapper;

    @Test
    public void testUpdateUUID(){
        String uuid = UUID.randomUUID().toString();
        adminMapper.updateUuid(1L,uuid);

    }

    @Test
    public void testClearUUID(){
        adminMapper.clearUuid(1L);
    }


}