package com.example.cinemakiosk.dto.requestDTO;

import lombok.Data;

import java.util.List;

@Data
public class TheaterRequest {
    private List<Long> ids;
    private Long changeValue; // TODO 이름 이렇게 정하긴했는데 애매할경우 변경
}
