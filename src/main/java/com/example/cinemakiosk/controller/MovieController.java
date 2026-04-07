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
@RequestMapping("/api/movie")
public class MovieController {


    // 이미지 저장 경로 (반환에서 사용)
    @Value("${my.upload.path}")
    private String uploadPath;

    private final MovieService movieService;



    // 영화등록
    @Operation(summary = "영화등록",
            description = "1. application.properties혹은 MovieServiceImpl에서 이미지 저장경로 변경\n 2. movieId = 0 지우기 (비우기)\n " +
                    "3. image = 사진 안올렸다면 Send empty value 체크 해제\n" +
                    "4. posterPath = tmdb/search 에서 찾은 posterPath 입력\n" +
                    "- image, posterPath 둘다 업로드시 posterPath 이미지로 저장됨")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> upload(@Valid @ModelAttribute MovieDTO movieDTO) {
        log.info("upload post...");
        movieService.insertMovie(movieDTO);
        return ResponseEntity.ok().build();
    }



    // 영화수정
    @Operation(summary = "영화수정",
            description = "1. 수정할 영화의 movieId 입력\n 2. image = 사진 안올렸다면 Send empty value 체크 해제")
    @PostMapping(value = "/modify", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> modify(@ModelAttribute MovieDTO movieDTO) {
        log.info("Modify post...");
        movieService.modify(movieDTO);
        return ResponseEntity.ok().build();
    }



    // 영화 상영종료 처리
    @Operation(summary = "영화 상영종료 처리",
            description = "1. 상영종료처리할 영화의 movieId 입력\n " +
                    "2. Schedule을 조회해 지나간 상영 시간중 가장 가까운 상영 시간을 end_at 시간으로 저장")
    @PostMapping(value = "/modifyEndAt")
    public ResponseEntity<Void> modifyEndAt(@RequestParam Long movieId) {
        log.info("modifyEndAt post...");
        log.info("modifyEndAt movieId = " + movieId);
        movieService.modifyEndAt(movieId);
        return ResponseEntity.ok().build();
    }



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

    // 전체 영화 조회
    @Operation(summary = "전체영화 조회 (관리자용)")
    @GetMapping("/realAll")
    public ResponseEntity<List<MovieDTO>> ManagerReadAll() {
        log.info("read get...");
        List<MovieDTO> movieDTOList = movieService.getAllMovies();
        log.info("movieDTOList: {}", movieDTOList);
        return ResponseEntity.ok(movieDTOList);
    }




    // 영화삭제
    @Operation(summary = "영화삭제",
            description = "삭제할 영화의 movieId 입력")
    @GetMapping("/remove")
    public ResponseEntity<Void> remove(@RequestParam Long movieId) {
        log.info("Remove post...");
        movieService.remove(movieId);
        return ResponseEntity.ok().build();
    }



//
//    // 영화 사진 반환
//    @Operation(summary = "영화 사진 반환",
//            description = "1. 영화 제목 입력시 영화 이미지 반환\n 2. 맨 앞에 s_ 붙일 시 썸네일 이미지 반환\n - 영화 제목이 중복일시 에러")
//    @GetMapping("/{titleName}")
//    public ResponseEntity<Resource> image(@PathVariable String titleName) {
//        try {
//            File directory = new File(uploadPath);
//
//            String title = titleName;
//
//            // 썸네일 요청일시
//            if (titleName.startsWith("s_")) {
//                title = titleName.substring(2);
//            }
//            // 얻은 영화 제목으로 movieId 찾기
//            MovieDTO movieDTO = movieService.getMovieByTitle(title);
//            Long movieId = movieDTO.getMovieId();
//
//            // movieId로 파일 찾기
//            File[] files = directory.listFiles((dir, name) -> {
//                int lastDot = name.lastIndexOf('.');
//                // 썸네일 요청일시
//                if (titleName.startsWith("s_")){
//                    return name.substring(0, lastDot).equals(String.valueOf("s_" + movieId));
//                }
//                return name.substring(0, lastDot).equals(String.valueOf(movieId));
//            });
//
//            // 파일이 존재하지 않는 경우
//            if (files == null || files.length == 0) {
//                return ResponseEntity.notFound().build();
//            }
//
//            File targetFile = files[0]; // 매칭되는 첫 번째 파일 선택
//            Resource resource = new FileSystemResource(targetFile);
//
//            HttpHeaders headers = new HttpHeaders();
//            // 실제 파일의 확장자에 맞는 MIME 타입을 추출 (jpg, png, webp같은)
//            headers.add("Content-Type", Files.probeContentType(targetFile.toPath()));
//
//            // 확장자와 파일반환
//            return ResponseEntity.ok().headers(headers).body(resource);
//
//        } catch (IOException e) {
//            throw new RuntimeException("파일을 읽는 중 오류가 발생했습니다.", e);
//        }
//    }


}
