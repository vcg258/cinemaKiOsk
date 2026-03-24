package com.example.cinemakiosk.service;

import com.example.cinemakiosk.domain.MovieEntity.Rating;
import com.example.cinemakiosk.dto.MovieDTO;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

@Log4j2
@SpringBootTest
class serviceTest {

    @Autowired
    private MovieServiceImpl moviceService;

    @Test
    void findAll() {
        moviceService.getMovie("테스트");
    }

    @Test
    void insert() {
        MovieDTO movieDTO = MovieDTO.builder()
                .genre("wpwp")
                .rating(Rating.FIFTEEN.getConversion())
                .actors("일반인1")
                .createAt(LocalDateTime.now())
                .startAt(LocalDateTime.now())
                .endAt(LocalDateTime.now())
                .description("일반인의 일반적인 일상")
                .title("하드")
                .director("일반적인 감독")
                .runtime(120L)
                .build();
        log.info(movieDTO.getRating());
        moviceService.insertMovie(movieDTO);
    }

    @Test
    void search() {
        List<MovieDTO> movieDTOList = moviceService.getMovie("특별");
        for (MovieDTO movieDTO : movieDTOList) {
            log.info(movieDTO);

        }
    }
}