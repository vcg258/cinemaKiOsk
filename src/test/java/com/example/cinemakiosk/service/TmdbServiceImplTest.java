package com.example.cinemakiosk.service;

import com.example.cinemakiosk.dto.MovieDTO;
import com.example.cinemakiosk.dto.TmdbMovieDTO;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Log4j2
class TmdbServiceImplTest {

    @Autowired
    private TmdbServiceImpl tmdbServiceImpl;

    @Test
    void getPopularMovies() {
        List<TmdbMovieDTO> popularMovies = tmdbServiceImpl.getPopularMovies(1);
        assertNotNull(popularMovies);
        for (TmdbMovieDTO movieDTO : popularMovies) {
            log.info("movieDTO: {} ", movieDTO);
        }
    }

    @Test
    void searchMovies() {
        List<TmdbMovieDTO> tmdbMovieDTOS = tmdbServiceImpl.searchMovies("어벤져스");
        assertNotNull(tmdbMovieDTOS);
        for (TmdbMovieDTO movieDTO : tmdbMovieDTOS) {
            log.info("movieDTO: {} ", movieDTO);
        }
    }

    @Test
    void searchMovieDetail() {
        MovieDTO movieDTO = tmdbServiceImpl.searchMovieDetail(2134L);
        assertNotNull(movieDTO);
        log.info("movieDTO: {} ", movieDTO);
    }

    @Test
    void downloadAndSavePoster() {
        try {
            tmdbServiceImpl.downloadAndSavePoster("https://image.tmdb.org/t/p/w500/sotBnRlFJ67cPisjAUXzbPxi9kC.jpg", "타임 머신");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}