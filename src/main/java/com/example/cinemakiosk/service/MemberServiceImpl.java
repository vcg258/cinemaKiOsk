package com.example.cinemakiosk.service;

import com.example.cinemakiosk.domain.MemberEntity;
import com.example.cinemakiosk.domain.PointHistoryEntity.Type;
import com.example.cinemakiosk.dto.MemberDTO;
import com.example.cinemakiosk.mapper.MemberMapper;
import com.example.cinemakiosk.mapper.PointHistoryMapper;
import com.example.cinemakiosk.repository.MemberRepository;
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

    /**
     * 신규 회원등록
     * @param phone 회원 PK
     */
    @Override
    public void createMember(String phone, Integer point) { // TODO 회원가입이 쉽기떄문에 일정 기간이 지나면 삭제 되는 기능 있으면 좋을듯
        if (memberRepository.existsByPhone(phone)) {
            log.warn("createMember... 회원 존재함 생성 불가능");
            return; // 회원이 존재하면 생성하지 않음
        }
        MemberDTO memberDTO = MemberDTO.builder()
                .phone(phone)
                .point(point)
                .createAt(LocalDateTime.now())
                .build();
        log.info("createMember memberDTO: {}", memberDTO);
        memberRepository.save(MemberDTO.toEntity(memberDTO));
    }

    /**
     * 포인트 업데이트
     * @param phone 회원 PK
     * @param point 포인트
     */
    @Override
    public void pointHistoryCreate(String phone, Integer point, Type type) {
        if (!memberRepository.existsByPhone(phone)) {
            log.warn("remainingPoint... 등록된 회원 정보가 존재하지 않습니다");
            return;
        }
        Integer amount = 0;
        if (type == Type.EARN) {
            amount += point;
        } else {
            amount -= point;
        }


//        pointHistoryRepository.save();

        MemberEntity member = memberRepository.findById(phone).orElseThrow();
        MemberDTO dto = MemberDTO.builder()
                .phone(member.getPhone())
                .point(member.getPoint() + amount)
                .createAt(member.getCreateAt())
                .build();
        log.info("remainingPoint dto: {}", dto);
        memberRepository.save(MemberDTO.toEntity(dto));

    }

//    /**
//     * 포인트 변경 포인트 처리 내역
//     * @param phone 회원 PK
//     */
//    @Override
//    public void pointHistoryCreate(String phone) {
//        MemberEntity member = memberRepository.findById(phone).orElseThrow();
//
//    }

    @Override
    public void pointHistoryCancel(Long no) {

    }
}
