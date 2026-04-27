package com.example.cinemakiosk.dto.Tmdb;

import com.example.cinemakiosk.dto.Tmdb.TmdbMovieDTO.TmdbMovieDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TmdbSearchResponseDTO {
    private List<TmdbMovieDTO> results;
}

