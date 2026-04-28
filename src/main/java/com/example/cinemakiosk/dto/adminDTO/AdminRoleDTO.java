package com.example.cinemakiosk.dto.adminDTO;

import lombok.*;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AdminRoleDTO {
    private Long id;        // 인덱스 (권한 저장 시 숫자 ID로 전송됨)
    private String roleName;  // Spring Security 권한 키 (예: ROLE_REFUND)
    private String roleDesc;  // 뷰에서 보여줄 권한 설명
    private String groupName; // 프론트 사이드바 섹션 그룹명 (예: 영화 관리)
}
