package com.example.cinemakiosk.dto.AdminDTO;

import lombok.*;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AdminRoleDTO {
    private Long id; // 인덱스
    private String roleName; // 권한이름
    private String roleDesc; // 권한이름(뷰에서 보여줄것)
}
