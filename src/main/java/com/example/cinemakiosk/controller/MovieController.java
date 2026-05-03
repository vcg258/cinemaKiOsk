package com.example.cinemakiosk.controller;

import com.example.cinemakiosk.dto.MovieDTO;
import com.example.cinemakiosk.service.MovieService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnailator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/movie")
public class MovieController {

    private final MovieService movieService;

    // 영화등록
    @Operation(summary = "영화등록",
            description = "1. application.properties혹은 MovieServiceImpl에서 이미지 저장경로 변경\n 2. movieId = 0 지우기 (비우기)\n " +
                    "3. endAt 기본값중 마지막 Z 지우기\n" +
                    "4. image = 사진 안올렸다면 Send empty value 체크 해제\n" +
                    "5. posterPath = tmdb/search 에서 찾은 posterPath 입력\n" +
                    "- image, posterPath 둘다 업로드시 posterPath 이미지로 저장됨")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> upload(@Valid @ModelAttribute MovieDTO movieDTO) {
        log.info("upload post...");
        movieService.insertMovie(movieDTO);
        return ResponseEntity.ok().build();
    }

    // 영화수정
    @Operation(summary = "영화수정",
            description = "- 수정할 영화의 movieId 입력\n - 이외는 영화등록과 동일")
    @PatchMapping(value = "/modify", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> modify(@ModelAttribute MovieDTO movieDTO) {
        log.info("Modify post...");
        movieService.modify(movieDTO);
        return ResponseEntity.ok().build();
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
