package com.example.cinemakiosk.service;

import com.example.cinemakiosk.domain.enums.Rating;
import com.example.cinemakiosk.dto.MovieDTO;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Log4j2
@SpringBootTest
class MovieServiceImplTest {

    @Autowired
    private MovieServiceImpl movieService;


    // 추가
    @Test
    void insertMovie() {
        LocalDateTime now = LocalDateTime.now();
        MovieDTO movieDTO = MovieDTO.builder()
                .genre("wpwp")
                .rating(Rating.FIFTEEN)
                .actors("일반인1")
                .createAt(LocalDate.now())
                .startAt(LocalDate.now())
                .endAt(LocalDateTime.now())
                .description("일반인의 일반적인 일상")
                .title("아이언맨3")
                .director("일반적인 감독")
                .runtime(120L)
                .build();
        log.info(movieDTO.getRating());
        movieService.insertMovie(movieDTO);
    }

    // 상세조회
    @Test
    void getMovieById() {
        MovieDTO movieDTO = movieService.getMovieById(26L);
        log.info(movieDTO);
    }

    // 제목으로 상세조회
    @Test
    void getMovieByTitle() {
        MovieDTO movieDTO = movieService.getMovieByTitle("아이언맨3");
        log.info(movieDTO);
    }

    // 전체 조회
    @Test
    void getAllMovies() {
        List<MovieDTO> movieDTOList = movieService.getAllMovies();
        for (MovieDTO movieDTO : movieDTOList) {
            log.info(movieDTO);
        }
    }

    @Test
    void getAllMoviesActivation() {
        movieService.getScreeningPeriodAllMovies().forEach(log::info);
    }

//    // 키워드로 조회
//    @Test
//    void getMovie() {
//        List<MovieDTO> movieDTOList = movieService.getMovie("특별");
//        for (MovieDTO movieDTO : movieDTOList) {
//            log.info(movieDTO);
//        }
//    }
//
//    // 장르로 조회
//    @Test
//    void findByGenre() {
//        List<MovieDTO> movieDTOList = movieService.findByGenre("wpwp");
//        for (MovieDTO movieDTO : movieDTOList) {
//            log.info(movieDTO);
//        }
//    }

//    // 수정
//    @Test
//    void modify() {
//        movieService.modify();
//    }

    // 삭제
}