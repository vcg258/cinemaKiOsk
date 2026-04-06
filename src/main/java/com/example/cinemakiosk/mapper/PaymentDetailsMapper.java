package com.example.cinemakiosk.mapper;

import com.example.cinemakiosk.vo.PaymentDetailsVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PaymentDetailsMapper {

    PaymentDetailsVO selectOneById(String uuid);

    List<PaymentDetailsVO> selectAll();
}
