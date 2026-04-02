package com.example.cinemakiosk.controller;

import com.example.cinemakiosk.dto.MovieDTO;
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
@RequestMapping("/api/movie")
public class MovieController {





    /*
    swagger 사용법 (null유무)
     */

    /* upload
    1. movieId null

     */



























    // 이미지 저장 경로
    @Value("${my.upload.path}")
    private String uploadPath;

    private final MovieService movieService;


    /**
     * 영화 등록
     * @param movieDTO 영화 정보
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void upload(@ModelAttribute MovieDTO movieDTO) {
        log.info("upload post...");
        movieService.insertMovie(movieDTO);
    }


    /**
     * 영화 수정
     * @param movieDTO
     */
    @PostMapping(value = "/modify", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void modify(@ModelAttribute MovieDTO movieDTO) {
        log.info("Modify post...");
        movieService.modify(movieDTO);
    }



    /**
     * 영화 삭제
     * @param movieId 영화 PK
     */
    @GetMapping("/remove")
    public void remove(@RequestParam Long movieId) {
        log.info("Remove post...");
        movieService.remove(movieId);
    }




    /**
     * 상영중(상영기간중)인 영화 조회
     * @return 현재 상영중인 영화
     */
    @GetMapping("/all")
    public List<MovieDTO> readAll() {
        log.info("screening_period get...");

        List<MovieDTO> movieDTOList = movieService.getScreeningPeriodAllMovies();
        log.info("movieDTOList: {}", movieDTOList);
        return movieDTOList;
    }

    /**
     * 전체 영화 조회
     * @param model
     * @return 현재 db에 저장된 모든 영화
     */
    @GetMapping("/realAll")
    public List<MovieDTO> ManagerReadAll(Model model) {
        log.info("read get...");
        List<MovieDTO> movieDTOList = movieService.getAllMovies();
        log.info("movieDTOList: {}", movieDTOList);
        return movieDTOList;
    }






    // 영화 사진 반환
    @GetMapping("/bigImage/{fileName}")
    public ResponseEntity<Resource> bigImage(@PathVariable String fileName) {
        try {
            File directory = new File(uploadPath);

            String title = fileName;

            // 썸네일 요청일시
            if (fileName.startsWith("s_")) {
                title = fileName.substring(2);
            }
            // 얻은 영화 제목으로 movieId 찾기
            MovieDTO movieDTO = movieService.getMovieByTitle(title);
            Long movieId = movieDTO.getMovieId();

            // movieId로 파일 찾기
            File[] files = directory.listFiles((dir, name) -> {
                int lastDot = name.lastIndexOf('.');
                // 썸네일 요청일시
                if (fileName.startsWith("s_")){
                    return name.substring(0, lastDot).equals(String.valueOf("s_" + movieId));
                }
                return name.substring(0, lastDot).equals(String.valueOf(movieId));
            });

            // 파일이 존재하지 않는 경우
            if (files == null || files.length == 0) {
                return ResponseEntity.notFound().build();
            }

            File targetFile = files[0]; // 매칭되는 첫 번째 파일 선택
            Resource resource = new FileSystemResource(targetFile);

            HttpHeaders headers = new HttpHeaders();
            // 실제 파일의 확장자에 맞는 MIME 타입을 동적으로 추출 (jpg, png, webp같은)
            headers.add("Content-Type", Files.probeContentType(targetFile.toPath()));

            // 확장자와 파일반환
            return ResponseEntity.ok().headers(headers).body(resource);

        } catch (IOException e) {
            throw new RuntimeException("파일을 읽는 중 오류가 발생했습니다.", e);
        }
    }

//
//    // 키워드로 조회
//    @GetMapping("/{keyWord}")
//    public void readKeyWord(@PathVariable String keyWord, Model model) {
//        log.info("keyWord: {}", keyWord);
//
//        model.addAttribute("movie", movieService.getMovie(keyWord));
//    }



    //영화 상태 관리
    //영화 상태 변경 처리



}
