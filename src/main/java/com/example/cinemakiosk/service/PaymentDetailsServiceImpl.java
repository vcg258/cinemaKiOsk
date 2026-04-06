package com.example.cinemakiosk.service;

import com.example.cinemakiosk.dto.PaymentDetailsDTO;
import com.example.cinemakiosk.mapper.PaymentDetailsMapper;
import com.example.cinemakiosk.repository.PaymentDetailsRepository;
import com.example.cinemakiosk.vo.PaymentDetailsVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentDetailsServiceImpl implements PaymentDetailsService{
    private final PaymentDetailsMapper paymentDetailsMapper;
    private final PaymentDetailsRepository paymentDetailsRepository;

    //결제 시도 및 결과 등록
    @Override
    public void create(PaymentDetailsDTO paymentDetailsDTO) {
        paymentDetailsRepository.save(PaymentDetailsDTO.toEntity(paymentDetailsDTO));
    }


    //결제 내역 조회
    @Override
    public PaymentDetailsDTO read(String uuid) {
        PaymentDetailsVO paymentDetailsVO = paymentDetailsMapper.selectOneById(uuid);
        return PaymentDetailsVO.toDTO(paymentDetailsVO);
    }


    //결제 내역 전체조회
    @Override
    public List<PaymentDetailsDTO> readAll() {
        List<PaymentDetailsDTO> paymentDetailsDTOS = new ArrayList<>();
        List<PaymentDetailsVO> paymentDetailsVOS = paymentDetailsMapper.selectAll();
        for (PaymentDetailsVO paymentDetailsVO : paymentDetailsVOS){
            paymentDetailsDTOS.add(PaymentDetailsVO.toDTO(paymentDetailsVO));
        }

        return paymentDetailsDTOS;
    }

    //결제 내역 변경
    @Override
    public void update(PaymentDetailsDTO paymentDetailsDTO) {
        paymentDetailsRepository.save(PaymentDetailsDTO.toEntity(paymentDetailsDTO));
    }


    //환불 진행. >> 이거 방법 다르게 찾기
    @Override
    public void cancel(String uuid) {
//        paymentDetailsRepository.deleteById(uuid);
        //특정 값만 찾아와서 해당 값에서 환불로 바꾸면 될건데... 응답이 어떻게 오는지 알아야함.
    }
}
