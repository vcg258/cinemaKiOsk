package com.example.cinemakiosk.controller;

import com.example.cinemakiosk.dto.MovieDTO;
import com.example.cinemakiosk.dto.upload.UploadFileDTO;
import com.example.cinemakiosk.dto.upload.UploadResultDTO;
import com.example.cinemakiosk.service.MovieService;
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
@RequestMapping("/view")
public class MovieController {

    // 이미지 저장 경로
    @Value("${my.upload.path}")
    private String uploadPath;

    private final MovieService movieService;


    // 영화 등록
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void upload(@ModelAttribute MovieDTO movieDTO) {
        movieService.insertMovie(movieDTO);
    }

    // 전체 영화 조회
    @GetMapping("/all")
    public void readAll(Long movieId, Model model) {
        log.info("movieId: {}", movieId);

        model.addAttribute("movie", movieService.getMovieById(movieId));
    }

    // 키워드로 조회
    @GetMapping("/{keyWord}")
    public void readKeyWord(@PathVariable String keyWord, Model model) {
        log.info("keyWord: {}", keyWord);

        model.addAttribute("movie", movieService.getMovie(keyWord));
    }

    // 영화 사진 반환
    @GetMapping("/{fileName}")
    public ResponseEntity<Resource> viewFile(@PathVariable String fileName) {
        // 파일 경로
        Resource resource = new FileSystemResource(uploadPath + File.separator + fileName);

        // 첨부파일의 컨텐츠 타입
        HttpHeaders headers = new HttpHeaders();

        try {
            // 타입 읽고 추가
            headers.add("Content-Type", Files.probeContentType(resource.getFile().toPath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        log.info("로그" + ResponseEntity.ok().headers(headers).body(resource));

        // 타입과 경로 반환
        return ResponseEntity.ok().headers(headers).body(resource);
    }



}
