package com.example.cinemakiosk.dto.adminDTO;

import com.example.cinemakiosk.domain.admindomain.AdminEntity;
import com.example.cinemakiosk.vo.AdminVO;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class AdminDTO {
    private Long adminId; // 관리자 인덱스
    private String loginId; // 로그인 아이디
    private String password;    // 로그인 요청 시 사용, 응답 시엔 null로 두면 됨
    private String name; // 관리자 이름
    private String adminPhone; // 관리자 전화번호
    private boolean level;      // false: 마스터, true: 알바
    private String refreshToken;          // 자동 로그인 토큰
    private LocalDateTime createAt; // 생성일

    private List<String> permissions; // 관리자 권한이 담긴 리스트 (resultMap)

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
                .refreshToken(adminDTO.getRefreshToken())
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
                .refreshToken(adminDTO.getRefreshToken())
                .createAt(adminDTO.getCreateAt())
                .permissions(adminDTO.getPermissions())
                .build();
    }
}