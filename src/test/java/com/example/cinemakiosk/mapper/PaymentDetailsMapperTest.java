package com.example.cinemakiosk.mapper;

import com.example.cinemakiosk.vo.PaymentDetailsVO;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Log4j2
@SpringBootTest
class PaymentDetailsMapperTest {
    @Autowired
    private PaymentDetailsMapper paymentDetailsMapper;

    @Test
    public void selectOne(){
        PaymentDetailsVO paymentDetailsVO = paymentDetailsMapper.selectOneById("11111111-1111-1111-1111-111111111111");
        log.info(paymentDetailsVO);
    }

    @Test
    public void selectAll(){
        List<PaymentDetailsVO> paymentDetailsVOS = paymentDetailsMapper.selectAll();
        log.info(paymentDetailsVOS);
    }

}