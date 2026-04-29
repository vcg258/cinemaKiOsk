package com.example.cinemakiosk.dto.tmdb.TmdbMovieDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class TmdbMovieDTO {
    private Long id;

    private String title;
    private List<GenreDTO> genres;

    private String overview;
    private Long runtime;

    @JsonProperty("poster_path")
    private String posterPath;


}
