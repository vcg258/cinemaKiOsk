package com.example.cinemakiosk.service;

import com.example.cinemakiosk.dto.PaymentDetailsDTO;

import java.util.List;

public class PaymentDetailsServiceImpl implements PaymentDetailsService{
    //결제 시도 및 결과 등록
    @Override
    public void create(PaymentDetailsDTO paymentDetailsDTO) {

    }


    //결제 내역 조회
    @Override
    public PaymentDetailsDTO read(Long no) {
        return null;
    }


    //결제 내역 전체조회
    @Override
    public List<PaymentDetailsDTO> readAll() {
        return List.of();
    }

    //결제 내역 변경
    @Override
    public void update(PaymentDetailsDTO paymentDetailsDTO) {

    }


    //환불 진행.
    @Override
    public void cancel(Long no) {

    }
}
