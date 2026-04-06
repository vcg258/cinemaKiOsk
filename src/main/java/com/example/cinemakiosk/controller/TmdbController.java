package com.example.cinemakiosk.controller;


import com.example.cinemakiosk.dto.MovieDTO;
import com.example.cinemakiosk.dto.TmdbMovieDTO;
import com.example.cinemakiosk.service.TmdbService;
import com.example.cinemakiosk.service.TmdbServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
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
    @Operation(summary = "영화 목록",
            description = "Tmdb기준 인기순으로 영화 정렬")
    @GetMapping("/popular")
    public ResponseEntity<List<TmdbMovieDTO>> getPopularMovies(@RequestParam(defaultValue = "1") int page) {
          // ResponseEntity로 통일할까?
        return ResponseEntity.ok(tmdbService.getPopularMovies(page));
    }

    // 2. 영화 검색
    @Operation(summary = "영화 검색",
            description = "- 영화 제목으로 검색\n " +
                    "- id, title, overview(description), poster_path 반환")
    @GetMapping("/search")
    public ResponseEntity<List<TmdbMovieDTO>> searchMovies(@RequestParam String title) {
        log.info("Search movies by {}", title);
        return ResponseEntity.ok(tmdbService.searchMovies(title));
    }

    // 3. 영화 상세
    @Operation(summary = "영화 상세",
            description = "- /search 에서 찾은 id로 검색\n - genre, runtime, director, description, actors 반환")
    @GetMapping("/{tmdbId}")
    public ResponseEntity<MovieDTO> getMovieDetail(@PathVariable Long tmdbId) {
        log.info("Get movie: {}", tmdbId);
        return ResponseEntity.ok(tmdbService.searchMovieDetail(tmdbId));

    }
}
