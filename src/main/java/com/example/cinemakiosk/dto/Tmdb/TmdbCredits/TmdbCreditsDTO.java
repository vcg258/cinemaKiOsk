package com.example.cinemakiosk.dto.Tmdb.TmdbCredits;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TmdbCreditsDTO {
    private List<CastDTO> cast;
    private List<CrewDTO> crew;
}
