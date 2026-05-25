package com.example.cinemakiosk.dto.requestDTO;

import lombok.Data;

@Data
public class SeatReleaseRequest {
    private String userId;
    private Long scheduleId;
}
