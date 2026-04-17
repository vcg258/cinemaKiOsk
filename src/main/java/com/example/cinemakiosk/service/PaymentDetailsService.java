package com.example.cinemakiosk.service;

import com.example.cinemakiosk.dto.PaymentDetailsDTO;

import java.util.List;

public interface PaymentDetailsService {
    //결제 시도 및 결과 등록
    void create(PaymentDetailsDTO paymentDetailsDTO);

    //결제 내역 조회
    PaymentDetailsDTO read(String uuid);

    //결제 내역 전체조회
    List<PaymentDetailsDTO> readAll();

    //결제 내역 변경
    void updateToReturn(PaymentDetailsDTO paymentDetailsDTO);

}
