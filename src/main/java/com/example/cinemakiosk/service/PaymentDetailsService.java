package com.example.cinemakiosk.service;

import com.example.cinemakiosk.dto.PaymentDetailsDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PaymentDetailsService {
    //결제 시도 및 결과 등록
    void create(PaymentDetailsDTO paymentDetailsDTO);

    //결제 내역 조회
    PaymentDetailsDTO read(String uuid);

    //결제 내역 전체조회
    Page<PaymentDetailsDTO> readAll(int page);

    //결제 내역 변경
    void updateToReturn(PaymentDetailsDTO paymentDetailsDTO);

}
