package com.example.cinemakiosk.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class TimeBaseEntity {
    @Column(updatable = false, columnDefinition = "DATETIME DEFAULT now()")
    @CreatedDate private LocalDateTime createAt;
}

/*
start_at = 적립정책 시작일, 영화 상영시작일, 할인 정책 시작일, 스케줄 상영시작 시간  (관리자가 지정?, 생성하자마자 바로?)
end_at = 적립정책 만료일, 영화 상영종료일, 할인 정책 만료일, 스케줄 상영 종료 시간  (관리자가 지정?, 생성하자마자 바로?)
create_at = 관리자 계정 생성 일자,회원 생성일, 영화 영화등록일, 예매내역 예매기준시, 포인트 변경일
 */