package com.example.cinemakiosk.service;

import com.example.cinemakiosk.dto.MovieDTO;

import java.util.List;

public interface MovieService {
    List<MovieDTO> getMovie(String keyWord);

    void insertMovie(MovieDTO movieDTO);
}
