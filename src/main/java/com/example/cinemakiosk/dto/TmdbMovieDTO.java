package com.example.cinemakiosk.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class TmdbMovieDTO {
    private Long id;

    private String title;
    private List<GenreDTO> genres;

    private String overview;
    private Long runtime;

    @JsonProperty("poster_path")
    private String posterPath;

    @Getter
    @Setter
    public static class GenreDTO {
        private String name;
    }
}
