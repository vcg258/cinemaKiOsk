//package com.example.cinemakiosk.service;
//
//import com.example.cinemakiosk.domain.MovieEntity;
//import com.example.cinemakiosk.dto.MovieDTO;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class MovieServiceImpl implements MovieService {
//
//
//
//    @Override
//    public List<MovieDTO> getMovie(String keyWord) {
//        List<MovieEntity> movieEntityList = movieMapper.findByTitleContaining(keyWord);
//
//        List<MovieDTO> movieDTOList = new ArrayList<>();
//        for (MovieEntity movieEntity : movieEntityList) {
//            movieDTOList.add(MovieDTO.from(movieEntity));
//        }
//        return movieDTOList;
//    }
//
//    @Override
//    public void insertMovie(MovieDTO movieDTO) {
//        movieMapper.insert(movieDTO.toEntity());  // toEntity() 사용
//    }
//}