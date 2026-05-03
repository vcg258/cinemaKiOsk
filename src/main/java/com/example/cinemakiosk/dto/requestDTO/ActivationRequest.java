package com.example.cinemakiosk.dto.requestDTO;

import lombok.Data;

import java.util.List;

@Data
public class ActivationRequest {
    private List<Long> ids; // 변경할 PKs
    private boolean activation;
}
