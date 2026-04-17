package com.example.cinemakiosk.service;

import com.example.cinemakiosk.domain.PaymentDetailsEntity;
import com.example.cinemakiosk.domain.enums.Status;
import com.example.cinemakiosk.dto.PaymentDetailsDTO;
import com.example.cinemakiosk.dto.ReservationDetailsDTO;
import com.example.cinemakiosk.mapper.PaymentDetailsMapper;
import com.example.cinemakiosk.repository.PaymentDetailsRepository;
import com.example.cinemakiosk.vo.PaymentDetailsVO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Log4j2
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
    @Transactional
    public void updateToReturn(PaymentDetailsDTO paymentDetailsDTO) {
        PaymentDetailsEntity entity = paymentDetailsRepository.findById(paymentDetailsDTO.getId()).orElseThrow();
        entity.changeStatus(Status.RETURN);
        entity.getReservationDetailsEntity().changeReturned(true);
        paymentDetailsRepository.save(entity);
    }

}
