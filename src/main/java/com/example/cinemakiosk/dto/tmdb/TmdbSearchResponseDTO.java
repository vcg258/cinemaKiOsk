package com.example.cinemakiosk.dto.tmdb;

import com.example.cinemakiosk.dto.tmdb.TmdbMovieDTO.TmdbMovieDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TmdbSearchResponseDTO {
    private List<TmdbMovieDTO> results;
}

