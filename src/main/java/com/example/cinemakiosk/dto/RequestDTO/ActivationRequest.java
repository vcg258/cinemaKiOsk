package com.example.cinemakiosk.dto.RequestDTO;

import lombok.Data;

import java.util.List;

@Data
public class ActivationRequest {
    private List<Long> ids; // 변경할 PKs
    boolean activation; // 변경할 만료여부
}
