package com.example.cinemakiosk.controller;

import com.example.cinemakiosk.dto.MovieDTO;
import com.example.cinemakiosk.service.MovieService;
import com.example.cinemakiosk.util.PosterStorageUtil;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;


@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/movie")
public class MovieController {

    private final MovieService movieService;
    private final PosterStorageUtil posterStorageUtil;

    // 포스터 저장 file 또는 imageUrl 중 하나 필수, 저장 후 posterPath(/uploads/파일명) 반환
    @Operation(summary = "포스터 이미지 저장",
            description = "file(MultipartFile) 또는 imageUrl 중 하나 전달\n파일명 규칙: title_yyyy-MM-dd[중복시(n)].ext")
    @PostMapping(value = "/poster", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> uploadPoster(
            @RequestParam("title") String title,
            @RequestParam("createAt") String createAt,
            @RequestPart(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "imageUrl", required = false) String imageUrl
    ) throws IOException {
        log.info("uploadPoster - title={}, createAt={}, hasFile={}, imageUrl={}",
                title, createAt, file != null && !file.isEmpty(), imageUrl);

        String posterPath;

        // file 우선 (posterStorage.mjs: handlePosterUpload() 동일 우선순위)
        if (file != null && !file.isEmpty()) {
            posterPath = posterStorageUtil.saveFromFile(title, createAt, file);
        } else if (StringUtils.hasText(imageUrl) && imageUrl.startsWith("http")) {
            posterPath = posterStorageUtil.saveFromUrl(title, createAt, imageUrl);
        } else {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "file 또는 imageUrl이 필요합니다."));
        }

        log.info("uploadPoster 완료 - posterPath={}", posterPath);
        return ResponseEntity.ok(Map.of("posterPath", posterPath));
    }

    // 영화등록
    @Operation(summary = "영화등록",
            description = "posterPath(/uploads/영화명_yyyy-MM-dd.jpg)만 전달\n" +
                    "movieId는 비우고, createAt은 yyyy-MM-dd 형식")
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
