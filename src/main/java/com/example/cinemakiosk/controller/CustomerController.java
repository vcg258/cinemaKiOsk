package com.example.cinemakiosk.controller;


import com.example.cinemakiosk.dto.MovieDTO;
import com.example.cinemakiosk.service.MovieService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CustomerController {

    @Autowired
    private final MovieService movieService;


    // 상영중인 영화 조회
    @Operation(summary = "오늘날짜에 스케쥴이 있는 영화 조회 (고객용)",
            description = "- 스케쥴을 조회해 영화정보를 불러오므로 영화정보의 start_at과 end_at은 노상관" )
    @GetMapping("/all")
    public ResponseEntity<List<MovieDTO>> readAll() {
        log.info("screening_period get...");

        List<MovieDTO> movieDTOList = movieService.getScreeningPeriodAllMovies();
        log.info("movieDTOList: {}", movieDTOList);
        return ResponseEntity.ok(movieDTOList);
    }


    // 단일 영화 조회 (고객 상세 페이지용)
    @Operation(summary = "단일 영화 조회", description = "movieId로 단일 영화 정보 조회")
    @GetMapping("/{movieId}/readOne")
    public ResponseEntity<MovieDTO> getMovieById(@PathVariable Long movieId) {
        log.info("getMovieById get... id={}", movieId);
        return ResponseEntity.ok(movieService.getMovieById(movieId));
    }

}
