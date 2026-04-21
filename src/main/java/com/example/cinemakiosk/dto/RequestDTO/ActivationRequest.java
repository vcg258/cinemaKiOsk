package com.example.cinemakiosk.dto.RequestDTO;

import lombok.Data;

import java.util.List;

@Data
public class ActivationRequest {
    private List<Long> ids; // 변경할 PKs
    private boolean activation;

}
