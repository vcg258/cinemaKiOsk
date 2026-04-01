package com.example.cinemakiosk.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TmdbSearchResponseDTO {
    private List<TmdbMovieDTO> results;
}

