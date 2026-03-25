package com.example.cinemakiosk.service;

import com.example.cinemakiosk.domain.MovieEntity;
import com.example.cinemakiosk.domain.Rating;
import com.example.cinemakiosk.dto.MovieDTO;

import java.util.List;

public interface MovieService {



    // 추가
    void insertMovie(MovieDTO movieDTO);

    // 상세 조회
    MovieDTO getMovieById(long movieId);

    //전체 조회
    List<MovieDTO> getAllMovies();

    // 제목 키워드로 조회
    List<MovieDTO> getMovie(String keyWord);

    // 장르로 조회
    List<MovieDTO> findByGenre(String genre);

    // 관람등급으로 조회
    List<MovieDTO> findByRating(Rating rating); // String으로 수정해야 하나?



}

