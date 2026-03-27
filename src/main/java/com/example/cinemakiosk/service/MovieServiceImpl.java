package com.example.cinemakiosk.service;

import com.example.cinemakiosk.domain.MovieEntity.MovieEntity;
import com.example.cinemakiosk.domain.MovieEntity.Rating;
import com.example.cinemakiosk.dto.MovieDTO;
import com.example.cinemakiosk.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;


    // 추가
    @Override
    public void insertMovie(MovieDTO movieDTO) {
        movieRepository.save(MovieDTO.toEntity(movieDTO));  // toEntity() 사용
    }

    // 상세 조회
    @Override
    public MovieDTO getMovieById(long movieId) {
        Optional<MovieEntity> optionalMovieEntity = movieRepository.findById(movieId);
        MovieEntity movieEntity = optionalMovieEntity.orElseThrow();
        MovieDTO movieDTO = MovieEntity.toDTO(movieEntity);
        return movieDTO;
    }

    //전체 조회
    @Override
    public List<MovieDTO> getAllMovies() {
        List<MovieEntity> movieEntityList = movieRepository.findAll();

        List<MovieDTO> movieDTOList = new ArrayList<>();
        for (MovieEntity movieEntity : movieEntityList) {
            movieDTOList.add(MovieEntity.toDTO(movieEntity));
        }
        return movieDTOList;
    }

    // 제목 키워드로 조회
    @Override
    public List<MovieDTO> getMovie(String keyWord) {
        List<MovieEntity> movieEntityList = movieRepository.findByTitleContaining(keyWord);

        List<MovieDTO> movieDTOList = new ArrayList<>();
        for (MovieEntity movieEntity : movieEntityList) {
            movieDTOList.add(MovieEntity.toDTO(movieEntity));
        }
        return movieDTOList;
    }



    // 장르로 조회
    @Override
    public List<MovieDTO> findByGenre(String genre) {
        List<MovieEntity> movieEntityList = movieRepository.findByGenre(genre);

        List<MovieDTO> movieDTOList = new ArrayList<>();
        for (MovieEntity movieEntity : movieEntityList) {
            movieDTOList.add(MovieEntity.toDTO(movieEntity));
        }
        return movieDTOList;
    }


    // 관람등급으로 조회
    @Override
    public List<MovieDTO> findByRating(Rating rating) {
        List<MovieEntity> movieEntityList = movieRepository.findByRating(rating);

        List<MovieDTO> movieDTOList = new ArrayList<>();

        for (MovieEntity movieEntity : movieEntityList) {
            movieDTOList.add(MovieEntity.toDTO(movieEntity));
        }

        return movieDTOList;
    }
}
