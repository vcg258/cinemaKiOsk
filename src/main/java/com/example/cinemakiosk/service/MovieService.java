package com.example.cinemakiosk.service;

import com.example.cinemakiosk.dto.MovieDTO;

import java.util.List;

public interface MovieService {
    // 추가
    void insertMovie(MovieDTO movieDTO);

    // 전체 조회
    List<MovieDTO> getAllMovies();

    // 수정
    void modify(MovieDTO movieDTO);

    // 삭제
    void remove(long movieId);

    // 상영종료처리
    void modifyEndAt(long movieId);
}

