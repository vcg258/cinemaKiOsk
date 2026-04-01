package com.example.cinemakiosk.controller;


import com.example.cinemakiosk.dto.MovieDTO;
import com.example.cinemakiosk.dto.TmdbMovieDTO;
import com.example.cinemakiosk.service.TmdbService;
import com.example.cinemakiosk.service.TmdbServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j2
@RestController
@RequestMapping("/tmdb")
@RequiredArgsConstructor
public class TmdbController {

    private final TmdbService tmdbService;

    // 1. 영화 검색
    @GetMapping("/search")
    public List<TmdbMovieDTO> searchMovies(@RequestParam String title) {
        return tmdbService.searchMovies(title);
    }

    // 2. 영화 선택
    @GetMapping("/{tmdbId}")
    public MovieDTO getMovieDetail(@PathVariable Long tmdbId) {
        return tmdbService.searchMovieDetail(tmdbId);
    }
}