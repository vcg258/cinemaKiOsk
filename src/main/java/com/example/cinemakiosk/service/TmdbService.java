package com.example.cinemakiosk.service;

import com.example.cinemakiosk.dto.MovieDTO;
import com.example.cinemakiosk.dto.TmdbMovieDTO;

import java.io.IOException;
import java.util.List;

public interface TmdbService {

    // 이미지 url 다운로드 후 저장
    void downloadAndSavePoster(String posterPath, String title) throws IOException;

    // 검색 목록
    // id, title, poster(image url)
    List<TmdbMovieDTO> searchMovies(String title);

    // 선택한 영화 ID로 상세조회
    // runtime, genres
    MovieDTO searchMovieDetail(Long tmdbId);

}
