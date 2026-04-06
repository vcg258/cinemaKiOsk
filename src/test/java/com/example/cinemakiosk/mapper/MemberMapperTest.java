package com.example.cinemakiosk.mapper;

import com.example.cinemakiosk.vo.MemberVO;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Log4j2
@SpringBootTest
class MemberMapperTest {
    @Autowired private MemberMapper memberMapper;

    @Test
    public void selectOneTest(){
        MemberVO memberVO = memberMapper.selectOneById("010-1234-5678");
        log.info(memberVO);
    }

    @Test
    public void selectAllTest(){
        List<MemberVO> memberVOS = memberMapper.selectAll();
        log.info(memberVOS);
    }


}