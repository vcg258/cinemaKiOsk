package com.example.cinemakiosk.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

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

    @Getter
    @Setter
    public static class GenreDTO { // TODO 분리
        private String name;
    }
}
