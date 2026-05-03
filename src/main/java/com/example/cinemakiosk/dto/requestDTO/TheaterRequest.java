package com.example.cinemakiosk.dto.requestDTO;

import lombok.Data;

import java.util.List;

@Data
public class TheaterRequest {
    private List<Long> ids;
    private Long changeValue;
}
