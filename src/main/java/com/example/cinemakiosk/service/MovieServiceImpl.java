package com.example.cinemakiosk.service;

import com.example.cinemakiosk.domain.MovieEntity.MovieEntity;
import com.example.cinemakiosk.dto.MovieDTO;
import com.example.cinemakiosk.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {


    private final MovieRepository movieRepository;

    @Override
    public List<MovieDTO> getMovie(String keyWord) {
        List<MovieEntity> movieEntityList = movieRepository.findByTitleContaining(keyWord);

        List<MovieDTO> movieDTOList = new ArrayList<>();
        for (MovieEntity movieEntity : movieEntityList) {
            movieDTOList.add(MovieDTO.ToDTO(movieEntity));
        }
        return movieDTOList;
    }

    @Override
    public void insertMovie(MovieDTO movieDTO) {
        movieRepository.save(MovieDTO.toEntity(movieDTO));  // toEntity() 사용
    }
}