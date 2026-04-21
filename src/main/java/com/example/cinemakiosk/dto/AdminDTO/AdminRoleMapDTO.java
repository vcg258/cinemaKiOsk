package com.example.cinemakiosk.dto.AdminDTO;

import lombok.*;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AdminRoleMapDTO {
    private Long id; // 인덱스
    private Long adminId; // 관리자인덱스 FK
    private Long roleId; // 권한인덱스 FK
}
