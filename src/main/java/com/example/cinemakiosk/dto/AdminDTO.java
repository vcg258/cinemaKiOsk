package com.example.cinemakiosk.dto;

import com.example.cinemakiosk.domain.adminDomain.AdminEntity;
import com.example.cinemakiosk.vo.AdminVO;
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
    private String uuid;          // 자동 로그인 토큰
    private LocalDateTime createAt;


    /**
     * DTO -> Entity
     * @param adminDTO
     * @return Entity
     */
    public static AdminEntity toEntity(AdminDTO adminDTO) {
        return AdminEntity.builder()
                .adminId(adminDTO.getAdminId())
                .loginId(adminDTO.getLoginId())
                .password(adminDTO.getPassword())  // 암호화된 비밀번호 저장
                .name(adminDTO.getName())
                .adminPhone(adminDTO.getAdminPhone())
                .level(adminDTO.isLevel())
                .uuid(adminDTO.getUuid())
                .createAt(adminDTO.getCreateAt())
                .build();
    }

    /**
     * DTO -> VO
     * @param adminDTO
     * @return VO
     */
    public static AdminVO toVO(AdminDTO adminDTO) {
        return AdminVO.builder()
                .adminId(adminDTO.getAdminId())
                .loginId(adminDTO.getLoginId())
                .password(adminDTO.getPassword())  // 암호화된 비밀번호 저장
                .name(adminDTO.getName())
                .adminPhone(adminDTO.getAdminPhone())
                .level(adminDTO.isLevel())
                .uuid(adminDTO.getUuid())
                .createAt(adminDTO.getCreateAt())
                .build();
    }
}