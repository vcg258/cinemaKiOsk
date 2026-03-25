package com.example.cinemakiosk.service;

import com.example.cinemakiosk.domain.MemberEntity;
import com.example.cinemakiosk.domain.PaymentDetailsEntity.PaymentDetailsEntity;
import com.example.cinemakiosk.domain.PointHistoryEntity.PointHistoryEntity;
import com.example.cinemakiosk.domain.PointHistoryEntity.Type;
import com.example.cinemakiosk.dto.MemberDTO;
import com.example.cinemakiosk.dto.PointHistoryDTO;
import com.example.cinemakiosk.mapper.MemberMapper;
import com.example.cinemakiosk.mapper.PointHistoryMapper;
import com.example.cinemakiosk.repository.MemberRepository;
import com.example.cinemakiosk.repository.PaymentDetailsRepository;
import com.example.cinemakiosk.repository.PointHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Log4j2
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{
    private final MemberRepository memberRepository;
    private final MemberMapper memberMapper;
    private final PointHistoryRepository pointHistoryRepository;
    private final PointHistoryMapper pointHistoryMapper;
    private final PaymentDetailsRepository paymentDetailsRepository;

    /**
     * 신규 회원등록 및 포인트 내역 추가
     * @param phone 회원 PK
     * @param point 포인트
     * @param paymentId 결제내역 PK
     */
    @Override
    public void createMember(String phone, Integer point, String paymentId) { // TODO 회원가입이 쉽기떄문에 일정 기간이 지나면 삭제 되는 기능 있으면 좋을듯
        if (memberRepository.existsByPhone(phone)) {
            log.warn("createMember... 회원 존재함 생성 불가능");
            return; // 회원이 존재하면 생성하지 않음
        }

        MemberDTO memberDTO = MemberDTO.builder()
                .phone(phone)
                .point(point)
                .build();
        log.info("createMember... memberDTO: {}", memberDTO);
        memberRepository.save(MemberDTO.toEntity(memberDTO));

        MemberEntity member = memberRepository.findById(phone).orElseThrow();
        PaymentDetailsEntity payment = paymentDetailsRepository.getReferenceById(paymentId);
        PointHistoryDTO pointHistoryDTO = PointHistoryDTO.builder()
                .paymentId(paymentId)
                .phone(phone)
                .type(Type.EARN)
                .amountPoint(point)
                .build();

        log.info("createMember... pointHistoryDTO: {}", pointHistoryDTO);
        pointHistoryRepository.save(PointHistoryDTO.toEntity(pointHistoryDTO, payment, member)); // 포인트 내역 추가

    }

    /**
     * 포인트 업데이트 및 포인트 내역 추가
     * @param phone 회원 PK
     * @param point 포인트
     * @param type 적립 / 사용 여부
     * @param paymentId 결제내역 PK
     */
    @Override
    public void pointHistoryCreate(String phone, Integer point, Type type, String paymentId) {
        if (!memberRepository.existsByPhone(phone)) { // 회원 내역없으면 return
            log.warn("pointHistoryCreate... 등록된 회원 정보가 존재하지 않습니다");
            return;
        }
        MemberEntity member = memberRepository.findById(phone).orElseThrow();
        PaymentDetailsEntity payment = paymentDetailsRepository.getReferenceById(paymentId);

        // 타입별 적립 / 사용
        Integer amount = member.getPoint();
        if (type == Type.EARN) {
            amount += point;
        } else {
            amount -= point;
        }
        PointHistoryDTO pointHistoryDTO = PointHistoryDTO.builder()
                .paymentId(paymentId)
                .phone(phone)
                .type(type)
                .amountPoint(point)
                .build();

        log.info("pointHistoryCreate... pointHistoryDTO: {}", pointHistoryDTO);
        pointHistoryRepository.save(PointHistoryDTO.toEntity(pointHistoryDTO, payment, member)); // 포인트 내역 추가

        member.changePoint(amount);
        memberRepository.save(member); // 회원 잔여포인트 업데이트

    }

    @Override
    public void pointHistoryCancel(Long no) {

    }
}
