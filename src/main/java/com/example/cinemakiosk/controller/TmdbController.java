package com.example.cinemakiosk.controller;


import com.example.cinemakiosk.dto.MovieDTO;
import com.example.cinemakiosk.dto.TmdbMovieDTO;
import com.example.cinemakiosk.service.TmdbService;
import com.example.cinemakiosk.service.TmdbServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j2
@RestController
@RequestMapping("/api/tmdb")
@RequiredArgsConstructor
public class TmdbController {

    private final TmdbService tmdbService;


    // 1. 인기 영화 목록
    @GetMapping("/popular")
    public ResponseEntity<List<TmdbMovieDTO>> getPopularMovies(@RequestParam(defaultValue = "1") int page) {
          // ResponseEntity로 통일할까?
        return ResponseEntity.ok(tmdbService.getPopularMovies(page));
    }

    // 2. 영화 검색
    @GetMapping("/search")
    public List<TmdbMovieDTO> searchMovies(@RequestParam String title) {
        log.info("Search movies by {}", title);
        return tmdbService.searchMovies(title);
    }

    // 3. 영화 선택
    @GetMapping("/{tmdbId}")
    public MovieDTO getMovieDetail(@PathVariable Long tmdbId) {
        log.info("Get movie: {}", tmdbId);
        return tmdbService.searchMovieDetail(tmdbId);
    }
}
