package com.example.cinemakiosk.service;

import com.example.cinemakiosk.dto.MovieDTO;

import java.util.List;

public interface MovieService {
    // 추가
    void insertMovie(MovieDTO movieDTO);

    // 상세 조회
    MovieDTO getMovieById(Long movieId);

    // 제목으로 상세 조회
    MovieDTO getMovieByTitle(String title);

    // 전체 조회
    List<MovieDTO> getAllMovies();

    // 현재 상영중인 영화 전체 조회
    List<MovieDTO> getScreeningPeriodAllMovies();

    // 수정
    void modify(MovieDTO movieDTO);

    // 삭제
    void remove(long movieId);

    // 상영종료처리
    void modifyEndAt(long movieId);
}

