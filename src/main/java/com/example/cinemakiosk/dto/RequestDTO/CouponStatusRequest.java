package com.example.cinemakiosk.dto.RequestDTO;

import lombok.Data;

import java.util.List;

@Data
public class CouponStatusRequest {
    private List<String> couponNums; // PKs
    private boolean status; // 변경할 사용여부
}
