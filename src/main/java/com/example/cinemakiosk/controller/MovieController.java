package com.example.cinemakiosk.controller;

import com.example.cinemakiosk.dto.MovieDTO;
import com.example.cinemakiosk.service.MovieService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/movie")
public class MovieController {

    private final MovieService movieService;

    // 영화등록
    @Operation(summary = "영화등록",
            description = "포스터 파일은 프론트 서버 uploads/ 에 저장됩니다. " +
                    "posterPath(/uploads/영화명_yyyy-MM-dd.jpg)만 전달하세요. " +
                    "movieId는 비우고, createAt은 yyyy-MM-dd 형식입니다.")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Boolean>> upload(@Valid @ModelAttribute MovieDTO movieDTO) {
        log.info("upload post...");
        movieService.insertMovie(movieDTO);
        return ResponseEntity.ok(Map.of("success", true));
    }

    // 영화수정
    @Operation(summary = "영화수정",
            description = "- 수정할 영화의 movieId 입력\n - 이외는 영화등록과 동일")
    @PatchMapping(value = "/modify", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Boolean>> modify(@ModelAttribute MovieDTO movieDTO) {
        log.info("Modify post...");
        movieService.modify(movieDTO);
        return ResponseEntity.ok(Map.of("success", true));
    }

    // 영화 상영종료 처리
    @Operation(summary = "영화 상영종료 처리",
            description = "1. 상영종료처리할 영화의 movieId 입력\n " +
                    "2. Schedule을 조회해 지나간 상영 시간중 가장 가까운 상영 시간을 end_at 시간으로 저장")
    @PatchMapping("/{movieId}/end")
    public ResponseEntity<Void> modifyEndAt(@PathVariable Long movieId) {
        log.info("modifyEndAt post...");
        log.info("modifyEndAt movieId = " + movieId);
        movieService.modifyEndAt(movieId);
        return ResponseEntity.ok().build();
    }

    // 전체 영화 조회
    @Operation(summary = "전체영화 조회 (관리자용)")
    @GetMapping("/readAll")
    public ResponseEntity<List<MovieDTO>> ManagerReadAll() {
        log.info("read get...");
        List<MovieDTO> movieDTOList = movieService.getAllMovies();
        log.info("movieDTOList: {}", movieDTOList);
        return ResponseEntity.ok(movieDTOList);
    }

    // 영화삭제
    @Operation(summary = "영화삭제",
            description = "삭제할 영화의 movieId 입력")
    @DeleteMapping("/remove")
    public ResponseEntity<Void> remove(@RequestParam Long movieId) {
        log.info("Remove post...");
        movieService.remove(movieId);
        return ResponseEntity.ok().build();
    }
}
