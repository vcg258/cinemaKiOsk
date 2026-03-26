package com.example.cinemakiosk.vo;

import com.example.cinemakiosk.domain.AdminEntity;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class AdminVO {

    private final Long adminId;
    private final String loginId;
    private final String name;
    private final String adminPhone;
    private final boolean level;           // false: 마스터, true: 알바
    private final LocalDateTime createAt;

    // Entity → VO 변환 (민감 정보 제외)
    public static AdminVO from(AdminEntity entity) {
        return AdminVO.builder()
                .adminId(entity.getAdminId())
                .loginId(entity.getLoginId())
                .name(entity.getName())
                .adminPhone(entity.getAdminPhone())
                .level(entity.isLevel())
                .createAt(entity.getCreateAt())
                .build();
    }
}