package com.example.cinemakiosk.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TmdbCreditsDTO { // TODO 분리
    private List<CastDTO> cast;
    private List<CrewDTO> crew;

    @Getter
    @Setter
    public static class CastDTO {
        private String name;
    }

    @Getter
    @Setter
    public static class CrewDTO {
        private String name;
        private String job;
    }
}
