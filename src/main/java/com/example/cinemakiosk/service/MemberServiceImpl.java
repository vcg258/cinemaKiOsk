package com.example.cinemakiosk.service;

import com.example.cinemakiosk.domain.MemberEntity;
import com.example.cinemakiosk.domain.PaymentDetailsEntity;
import com.example.cinemakiosk.domain.PointHistoryEntity;
import com.example.cinemakiosk.domain.enums.Type;
import com.example.cinemakiosk.dto.MemberDTO;
import com.example.cinemakiosk.dto.PaymentDetailsDTO;
import com.example.cinemakiosk.dto.PointHistoryDTO;
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
    private final PointHistoryRepository pointHistoryRepository;
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
                .createAt(LocalDateTime.now())
                .build();
        log.info("createMember... 신규 회원 정보 : {}", memberDTO);
        memberRepository.save(MemberDTO.toEntity(memberDTO));


        PointHistoryDTO pointHistoryDTO = PointHistoryDTO.builder()
                .paymentId(paymentId) // TODO 26-03-28 수정 해야함
                .phone(phone)
                .type(Type.EARN)
                .amountPoint(point)
                .build();

        log.info("createMember... 신규 회원 포인트 내역 추가 : {}", pointHistoryDTO);
        pointHistoryRepository.save(PointHistoryDTO.toEntity(pointHistoryDTO)); // 포인트 내역 추가

    }

    /**
     * 포인트 업데이트 및 포인트 내역 추가
     * @param pointHistoryDTO 포인트 내역의 DTO
     */
    @Override
    public void pointHistoryCreate(PointHistoryDTO pointHistoryDTO) {
        if (!memberRepository.existsByPhone(pointHistoryDTO.getPhone().getPhone())) { // 회원 내역없으면 return
            log.warn("pointHistoryCreate... 등록된 회원 정보가 존재하지 않습니다");
            return;
        }

        MemberEntity member = memberRepository.findById(pointHistoryDTO.getPhone().getPhone()).orElseThrow();

        // 음수 예외처리
        if (pointHistoryDTO.getType() == Type.USE && member.getPoint() == 0) {
            log.warn("pointHistoryCreate... 잔여 포인트 없음");
            return;
        }

        // 타입별 적립 / 사용
        Type type = pointHistoryDTO.getType();
        int amount = type == Type.EARN ?
                member.getPoint() + pointHistoryDTO.getAmountPoint() : member.getPoint() - pointHistoryDTO.getAmountPoint();

        PointHistoryDTO dto = PointHistoryDTO.builder()
                .paymentId(pointHistoryDTO.getPaymentId())
                .phone(pointHistoryDTO.getPhone())
                .type(type)
                .amountPoint(pointHistoryDTO.getAmountPoint())
                .build();

        log.info("pointHistoryCreate... 포인트 업데이트 내역 추가 : {}", dto);

        PointHistoryEntity pointHistory = pointHistoryRepository.save(PointHistoryDTO.toEntity(dto)); // 포인트 내역 추가
        log.info("pointHistoryCreate... 포인트 업데이트 내역 : {}", pointHistory);

        member.setPoint(amount);
        memberRepository.save(member); // 회원 잔여포인트 업데이트

    }

    /**
     * 환불처리시 포인트 복구처리
     * @param pointHistoryDTO 포인트 내역의 DTO
     */
    @Override
    public void pointHistoryCancel(PointHistoryDTO pointHistoryDTO) {
        MemberEntity member = memberRepository.findById(pointHistoryDTO.getPhone().getPhone()).orElseThrow(); // 해당 회원
        log.info("pointHistoryCancel... 해당 회원 : {}", member);
        log.info("pointHistoryCancel... 현재 포인트: {}", member.getPoint());

        // pointId로 한개 조회
        PointHistoryEntity pointHistory = pointHistoryRepository.findById(pointHistoryDTO.getPointId()).orElseThrow();

        // 해당 내역이 이 결제의 것인지 검증 (결제내역의 PK를 가져와 포인트내역의 PK와 비교함)
        if (!pointHistory.getPaymentDetailsEntity().getId().equals(pointHistoryDTO.getPaymentId().getId())) {
            log.error("pointHistoryCancel... 결제 내역 불일치");
            return;
        }

        // 이미 환불된 내역 체크 (해당 포인트 내역에서 PK를 조회 했는데 환불 타입이 있다면 이미 환불처리)
        if (pointHistory.getType() == Type.REFUND_EARN || pointHistory.getType() == Type.REFUND_USE) {
            log.warn("pointHistoryCancel... 이미 환불처리된 내역");
            return;
        }

        // 음수 예외처리
        if (pointHistoryDTO.getType() == Type.USE && member.getPoint() == 0) {
            log.warn("pointHistoryCancel... 잔여 포인트 없음");
            return;
        }

        // 타입이 EARN일 경우 REFUND_EARN 아니면 REFUND_USE
        Type type = pointHistory.getType() == Type.EARN ? Type.REFUND_EARN : Type.REFUND_USE;

        // 타입이 EARN일 경우 현재 포인트에서 빼고 아니면 더함
        Integer amountPoint = pointHistory.getType() == Type.EARN ?
                member.getPoint() - pointHistory.getAmountPoint() : member.getPoint() + pointHistory.getAmountPoint(); // 현재 포인트


        log.info("pointHistoryCancel... 환불 타입 : {}", type);
        log.info("pointHistoryCancel... 변경후 포인트 : {}", amountPoint);

        Integer changePoint = Math.abs(amountPoint - member.getPoint()); // 절댓값으로 변동 포인트 지정
        PointHistoryDTO dto = PointHistoryDTO.builder()
                .paymentId(pointHistoryDTO.getPaymentId())
                .phone(pointHistoryDTO.getPhone())
                .type(type)
                .amountPoint(changePoint)
                .build();
        log.info("pointHistoryCancel... 환불후 추가할 포인트 내역 : {}", dto);

        // DTO -> Entity 변환을 위해 지정
        PaymentDetailsEntity payment = paymentDetailsRepository.getReferenceById(pointHistoryDTO.getPaymentId().getId());
        PointHistoryEntity pointHistoryEntity = pointHistoryRepository.save(PointHistoryDTO.toEntity(dto)); // 포인트 내역 추가
        log.info("pointHistoryCancel... 포인트 추가내용 : {}", pointHistoryEntity);

        member.setPoint(amountPoint);
        memberRepository.save(member); // 회원 포인트 업데이트
    }
}
