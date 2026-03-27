package com.example.cinemakiosk.service;

import com.example.cinemakiosk.domain.MovieEntity.MovieEntity;
import com.example.cinemakiosk.domain.MovieEntity.Rating;
import com.example.cinemakiosk.dto.MovieDTO;
import com.example.cinemakiosk.dto.MovieRequestDTO;
import com.example.cinemakiosk.dto.MovieResponseDTO;
import com.example.cinemakiosk.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnailator;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Log4j2
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;

    // 이미지 저장 경로
    @Value("${my.upload.path}")
    private String uploadPath;

    // 추가
    @Override
    public void insertMovie(MovieDTO movieDTO) {

        log.info("movieDTO: {} ", movieDTO);

        // -------- 영화 정보 저장 -------- //
        movieRepository.save(MovieDTO.toEntity(movieDTO));


        // -------- 영화 이미지 저장 -------- //

        String movieName = movieDTO.getTitle();
        log.info("movieName : {} ", movieName);


        // 1. 저장할 파일이름 처리
        // startAt
        String startAt = movieDTO.getStartAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // 원본 파일명 사용
        MultipartFile file = movieDTO.getImage();
        log.info("file: {} ", file);
        String originalFilename = file.getOriginalFilename();
        log.info("originalFilename : {} ", originalFilename);



        Path path = Paths.get(uploadPath, originalFilename);

        log.info("path : {} ", path);


        // 2. 파일을 저장
        // 섬네일 이미지는 업로드하는 파일이 이미지일 때만 처리하도록 구성
        // 파일 이름은 맨 앞에 's_'로 시작하도록 구성.


        try {
            file.transferTo(path); // 실제 파일 저장

            // (업로드된 파일이 아니라) 저장된 파일(path)이 이미지 파일인지 검사
            String contentType = Files.probeContentType(path);
            if (contentType != null && contentType.startsWith("image")) {
                File thumbnailFile = new File(uploadPath, "s_" + originalFilename);
                Thumbnailator.createThumbnail(path.toFile(), thumbnailFile, 200, 200);

                log.info("thumbnailFile: {}", thumbnailFile);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }









    // 상세 조회
    @Override
    public MovieDTO getMovieById(long movieId) {
        Optional<MovieEntity> optionalMovieEntity = movieRepository.findById(movieId);
        MovieEntity movieEntity = optionalMovieEntity.orElseThrow();
        MovieDTO movieDTO = MovieDTO.toDTO(movieEntity);

        return movieDTO;
    }

    // 제목으로 상세 조회
    @Override
    public MovieDTO getMovieByTitle(String title) {
        Optional<MovieEntity> optionalMovieEntity = movieRepository.findByTitle(title);
        MovieEntity movieEntity = optionalMovieEntity.orElseThrow();
        MovieDTO movieDTO = MovieDTO.toDTO(movieEntity);

        return movieDTO;
    }

    //전체 조회
    @Override
    public List<MovieDTO> getAllMovies() {
        List<MovieEntity> movieEntityList = movieRepository.findAll();

        List<MovieDTO> movieDTOList = new ArrayList<>();
        for (MovieEntity movieEntity : movieEntityList) {
            movieDTOList.add(MovieDTO.toDTO(movieEntity));
        }
        return movieDTOList;
    }

    // 제목 키워드로 조회
    @Override
    public List<MovieDTO> getMovie(String keyWord) {
        List<MovieEntity> movieEntityList = movieRepository.findByTitleContaining(keyWord);

        List<MovieDTO> movieDTOList = new ArrayList<>();
        for (MovieEntity movieEntity : movieEntityList) {
            movieDTOList.add(MovieDTO.toDTO(movieEntity));
        }
        return movieDTOList;
    }



    // 장르로 조회
    @Override
    public List<MovieDTO> findByGenre(String genre) {
        List<MovieEntity> movieEntityList = movieRepository.findByGenre(genre);

        List<MovieDTO> movieDTOList = new ArrayList<>();
        for (MovieEntity movieEntity : movieEntityList) {
            movieDTOList.add(MovieDTO.toDTO(movieEntity));
        }
        return movieDTOList;
    }


    // 관람등급으로 조회
    @Override
    public List<MovieDTO> findByRating(Rating rating) {
        List<MovieEntity> movieEntityList = movieRepository.findByRating(rating);

        List<MovieDTO> movieDTOList = new ArrayList<>();

        for (MovieEntity movieEntity : movieEntityList) {
            movieDTOList.add(MovieDTO.toDTO(movieEntity));
        }

        return movieDTOList;
    }

    @Override
    public MovieResponseDTO<MovieDTO> getList(MovieRequestDTO movieRequestDTO) {
        String[] types = movieRequestDTO.getTypes();
        String keyword = movieRequestDTO.getKeyword();
//        movieRepository


        return null;
    }

//    @Override
//    public PageResponseDTO<BoardDTO> getList(MovieDTO movieDTO) {
//        // 1. searchAll() 실행
//        String[] types = movieDTO.getTypes();
//        String keyword = movieDTO.getKeyword();
//        Pageable pageable = movieDTO.getPageAble("bno");
//        Page<Board> result = boardRepository.searchAll(types, keyword, pageable);
//
//        // 2. PageResponseDTO 생성 후 반환
//        List<BoardDTO> dtoList = new ArrayList<>();
//        for (Board board : result.getContent()) {
//            dtoList.add(modelMapper.map(board, BoardDTO.class));
//        }
//        return PageResponseDTO.<BoardDTO>withAll()
//                .pageRequestDTO(pageRequestDTO)
//                .total((int) result.getTotalElements())
//                .dtoList(dtoList)
//                .build();
//    }
}
