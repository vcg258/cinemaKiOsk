package com.example.cinemakiosk.service;

import com.example.cinemakiosk.dto.PaymentDetailsDTO;
import com.example.cinemakiosk.dto.ReservationDetailsDTO;

import java.util.List;

public interface PaymentDetailsService {
    //결제 시도 및 결과 등록
    public void create(PaymentDetailsDTO paymentDetailsDTO);

    //결제 내역 조회
    public PaymentDetailsDTO read(String uuid);

    //결제 내역 전체조회
    public List<PaymentDetailsDTO> readAll();

    //결제 내역 변경
    public void update(PaymentDetailsDTO paymentDetailsDTO);

}
