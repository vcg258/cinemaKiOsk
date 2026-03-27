package com.example.cinemakiosk.service;

import com.example.cinemakiosk.domain.enums.Rating;
import com.example.cinemakiosk.dto.MovieDTO;
import com.example.cinemakiosk.dto.MovieRequestDTO;
import com.example.cinemakiosk.dto.MovieResponseDTO;

import java.util.List;

public interface MovieService {



    // 추가
    void insertMovie(MovieDTO movieDTO);

    // 상세 조회
    MovieDTO getMovieById(long movieId);

    // 제목으로 상세 조회 (image용, 더 필요하나?)
    MovieDTO getMovieByTitle(String title);

    // 전체 조회
    List<MovieDTO> getAllMovies();

    // 제목 키워드로 조회
    List<MovieDTO> getMovie(String keyWord);

    // 장르로 조회
    List<MovieDTO> findByGenre(String genre);

    // 관람등급으로 조회
    List<MovieDTO> findByRating(Rating rating); //

    // 여러 조건 조회
    MovieResponseDTO<MovieDTO> getList(MovieRequestDTO movieRequestDTO);
}

