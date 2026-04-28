package com.example.cinemakiosk.dto.requestDTO;

import lombok.Data;

import java.util.List;

@Data
public class AdminRoleMapRequest {
    private List<Long> roles; // 여러 권한을 담을 리스트
    private Long adminId; // 권한을 부여할 관리자 아이디
}
