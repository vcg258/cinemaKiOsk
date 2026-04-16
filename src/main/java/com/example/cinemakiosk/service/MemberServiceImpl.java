package com.example.cinemakiosk.service;

import com.example.cinemakiosk.domain.MemberEntity;
import com.example.cinemakiosk.domain.PaymentDetailsEntity;
import com.example.cinemakiosk.domain.PointHistoryEntity;
import com.example.cinemakiosk.domain.enums.Type;
import com.example.cinemakiosk.dto.MemberDTO;
import com.example.cinemakiosk.dto.PointHistoryDTO;
import com.example.cinemakiosk.mapper.PointHistoryMapper;
import com.example.cinemakiosk.repository.MemberRepository;
import com.example.cinemakiosk.repository.PaymentDetailsRepository;
import com.example.cinemakiosk.repository.PointHistoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Log4j2
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{
    private final MemberRepository memberRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final PaymentDetailsRepository paymentDetailsRepository;
    private final PointHistoryMapper pointHistoryMapper;

    /**
     * 신규 회원등록
     * @param memberDTO 회원 DTO
     */
    @Override
    public void createMember(MemberDTO memberDTO) {
        if (memberRepository.existsByPhone(memberDTO.getPhone())) {
            throw new IllegalStateException("회원 존재함 생성 불가능");
        }

        MemberDTO dto = MemberDTO.builder()
                .phone(memberDTO.getPhone())
                .point(memberDTO.getPoint())
                .createAt(LocalDateTime.now())
                .build();
        log.info("createMember... 신규 회원 정보 : {}", dto);
        memberRepository.save(MemberDTO.toEntity(dto));
    }

    /**
     * 포인트 업데이트 및 포인트 내역 추가
     * @param pointHistoryDTO 포인트 내역의 DTO
     */
    @Override
    @Transactional
    public void pointHistoryCreate(PointHistoryDTO pointHistoryDTO) {

        // 회원 정보 존재하지 않을 경우
        MemberEntity member = memberRepository.findById(pointHistoryDTO.getPhone()).orElse(null);
        if (member == null) {
            throw new NoSuchElementException("등록된 회원 정보가 존재하지 않습니다");
        }

        // 잔여포인트 보다 사용금액이 더 많으면 예외처리
        if (pointHistoryDTO.getAmountPoint() > member.getPoint()) {
            throw new IllegalStateException("포인트 부족");
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
                .createAt(LocalDateTime.now())
                .build();

        log.info("pointHistoryCreate... 포인트 업데이트 내역 추가 : {}", dto);

        PointHistoryEntity pointHistory = pointHistoryRepository.save(PointHistoryDTO.toEntity(dto)); // 포인트 내역 추가
        log.info("pointHistoryCreate... 포인트 업데이트 내역 : {}", pointHistory);

        member.changePoint(amount);
//        memberRepository.save(member); // 회원 잔여포인트 업데이트

    }

    /**
     * 환불처리시 포인트 복구처리
     * @param pointHistoryDTO 포인트 내역의 DTO
     */
    @Override
    @Transactional
    public void pointHistoryCancel(PointHistoryDTO pointHistoryDTO) {
        MemberEntity member = memberRepository.findById(pointHistoryDTO.getPhone()).orElseThrow(); // 해당 회원
        log.info("pointHistoryCancel... 해당 회원 : {}", member);
        log.info("pointHistoryCancel... 현재 포인트: {}", member.getPoint());

        // pointId로 한개 조회
        PointHistoryEntity pointHistory = pointHistoryRepository.findById(pointHistoryDTO.getPointId()).orElseThrow();
        log.info("pointHistoryCancel... 해당 회원 포인트 내역 : {}", pointHistory);

        // 해당 내역이 이 결제의 것인지 검증 (결제내역의 PK를 가져와 포인트내역의 PK와 비교함)
        if (!pointHistory.getPaymentDetailsEntity().getId().equals(pointHistoryDTO.getPaymentId())) {
            throw new NoSuchElementException("결제 내역 불일치");
        }

        // 이미 환불된 내역 체크 (해당 포인트 내역에서 PK를 조회 했는데 환불 타입이 있다면 이미 환불처리)
        if (pointHistory.getType() == Type.REFUND_EARN || pointHistory.getType() == Type.REFUND_USE) {
            throw new IllegalStateException("이미 환불처리된 내역");
        }

        // 음수 예외처리
        if (pointHistoryDTO.getType() == Type.USE && member.getPoint() == 0) {
            throw new IllegalStateException("잔여 포인트 없음");
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
                .createAt(LocalDateTime.now())
                .build();
        log.info("pointHistoryCancel... 환불후 추가할 포인트 내역 : {}", dto);

//        PointHistoryEntity pointHistoryEntity = pointHistoryRepository.save(PointHistoryDTO.toEntity(dto)); // 포인트 내역 추가
//        log.info("pointHistoryCancel... 포인트 추가내용 : {}", pointHistoryEntity);

        member.changePoint(amountPoint);
//        memberRepository.save(member); // 회원 포인트 업데이트
    }

    /**
     * 회원 전체 조회
     * @return 회원 전체를 담은 리스트
     */
    @Override
    public List<MemberDTO> getMembersAll() {
        List<MemberEntity> entityList = memberRepository.findAll();
        return entityList.stream().map(MemberEntity::toDTO).toList();
    }

    /**
     * 지정 회원 전체 로그 조회
     * @param phone 회원 PK
     * @return 지정 회원 전체 로그 조회
     */
    @Override
    public List<PointHistoryDTO> getMembersAllLog(String phone) {
        List<PointHistoryEntity> entity = pointHistoryRepository.findByMemberEntity_Phone(phone);
        return entity.stream().map(PointHistoryEntity::toDTO).toList();
    }

    /**
     * 포인트 내역 전체 조회
     * @return 전체 포인트내역이 담긴 리스트
     */
    @Override
    public List<PointHistoryDTO> getPointHistoryAll() {
        List<PointHistoryDTO> pointHistoryDTO = pointHistoryMapper.selectByMovieNameAll();
        return pointHistoryDTO.stream().toList();
    }

    /**
     * 회원 단일 조회
     * @param phone 회원 PK
     * @return 지정 회원
     */
    @Override
    public MemberDTO getMember(String phone) {
        MemberEntity entity = memberRepository.findById(phone).orElseThrow();
        return MemberEntity.toDTO(entity);
    }

    /**
     * 회원 포인트 갱신하는 기능
     * @param memberDTO
     */
    @Override
    public void updateMember(MemberDTO memberDTO) {
        MemberEntity entity = MemberDTO.toEntity(memberDTO);
        memberRepository.save(entity);
    }
}
