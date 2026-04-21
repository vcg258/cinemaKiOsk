package com.example.cinemakiosk.vo;

import com.example.cinemakiosk.dto.AdminDTO.AdminDTO;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class AdminVO {

    private Long adminId;
    private String loginId;
    private String password;    // 로그인 요청 시 사용, 응답 시엔 null로 두면 됨
    private String name;
    private String adminPhone;
    private boolean level;      // false: 마스터, true: 알바
    private String uuid;          // 자동 로그인 토큰
    private LocalDateTime createAt;

    private List<String> permissions; // 관리자 권한이 담긴 리스트 (resultMap)

    /**
     * VO -> DTO
     * @param adminVO
     * @return DTO
     */
    public static AdminDTO toDTO(AdminVO adminVO) {
        return AdminDTO.builder()
                .adminId(adminVO.getAdminId())
                .loginId(adminVO.getLoginId())
                .password(adminVO.getPassword())  // 암호화된 비밀번호 저장
                .name(adminVO.getName())
                .adminPhone(adminVO.getAdminPhone())
                .level(adminVO.isLevel())
                .uuid(adminVO.getUuid())
                .createAt(adminVO.getCreateAt())
                .permissions(adminVO.getPermissions())
                .build();
    }
}