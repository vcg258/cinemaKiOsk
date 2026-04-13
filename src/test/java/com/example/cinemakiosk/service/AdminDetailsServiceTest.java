package com.example.cinemakiosk.service;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@Log4j2
@SpringBootTest
class AdminDetailsServiceTest {
    @Autowired private PasswordEncoder passwordEncoder;

    @Test
    public void encodeTest(){
        String encodedPassword = passwordEncoder.encode("1234");
        log.info("encodedPassword: {}", encodedPassword);
    }

}