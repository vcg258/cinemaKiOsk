package com.example.cinemakiosk.dto;

import com.example.cinemakiosk.domain.AdminEntity;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class AdminDTO {

    private Long adminId;
    private String loginId;
    private String password;    // 로그인 요청 시 사용, 응답 시엔 null로 두면 됨
    private String name;
    private String adminPhone;
    private boolean level;      // false: 마스터, true: 알바
    private LocalDateTime createAt;

    // Entity → DTO 변환 (password 제외)
    public static AdminDTO from(AdminEntity entity) {
        return AdminDTO.builder()
                .adminId(entity.getAdminId())
                .loginId(entity.getLoginId())
                .name(entity.getName())
                .adminPhone(entity.getAdminPhone())
                .level(entity.isLevel())
                .createAt(entity.getCreateAt())
                .build();
    }

    // DTO → Entity 변환
    public AdminEntity toEntity(String encodedPassword) {
        return AdminEntity.builder()
                .loginId(this.loginId)
                .password(encodedPassword)  // 암호화된 비밀번호 저장
                .name(this.name)
                .adminPhone(this.adminPhone)
                .level(this.level)
                .createAt(LocalDateTime.now())
                .build();
    }
}