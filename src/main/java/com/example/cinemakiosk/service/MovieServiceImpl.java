package com.example.cinemakiosk.service;

import com.example.cinemakiosk.domain.MovieEntity;
import com.example.cinemakiosk.domain.enums.Rating;
import com.example.cinemakiosk.dto.MovieDTO;
import com.example.cinemakiosk.dto.MovieRequestDTO;
import com.example.cinemakiosk.dto.MovieResponseDTO;
import com.example.cinemakiosk.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnailator;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Log4j2
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;
    private final RestTemplate restTemplate;

    // 이미지 저장 경로
    @Value("${my.upload.path}")
    private String uploadPath;

    // 등록
    @Override
    public void insertMovie(MovieDTO movieDTO) {
        log.info("movieDTO: {} ", movieDTO);

        // 영화 정보 저장
        MovieEntity movieEntity = movieRepository.save(MovieDTO.toEntity(movieDTO));
        String filename = movieEntity.getMovieId() + ".jpg";  // movieId를 파일명으로

        // 영화 이미지 저장
        try {
            saveImageFromDTO(movieDTO, filename);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    // 수정
    @Override
    public void modify(MovieDTO movieDTO) {

        // 1. 기존 데이터 들고옴
        MovieEntity movieEntity = movieRepository.findById(movieDTO.getMovieId())
                .orElseThrow();

        // 2. 전체 수정
        movieEntity.update(movieDTO);
        movieRepository.save(movieEntity);

        // 3. 새 이미지가 있을 때만 처리
        MultipartFile file1 = movieDTO.getImage();
        String file2 = movieDTO.getPosterPath();

        if ((file1 != null && !file1.isEmpty()) || (file2 != null && !file2.isEmpty())) {
            String filename = movieDTO.getMovieId() + ".jpg";  // movieId로 파일명

            // 기존 이미지 삭제
            Path oldPath = Paths.get(uploadPath, filename);
            Path oldThumbPath = Paths.get(uploadPath, "s_" + filename);
            try {
                Files.deleteIfExists(oldPath);
                Files.deleteIfExists(oldThumbPath);
            } catch (IOException e) {
                log.warn("기존 이미지 삭제 실패");
            }

            // 영화 이미지 저장
            try {
                saveImageFromDTO(movieDTO, filename);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }



        }
    }
    // 이미지 삭제 필터
    private void saveImageFromDTO(MovieDTO movieDTO, String filename) throws IOException {
        // TMDB로 등록한 경우
        if (movieDTO.getPosterPath() != null && movieDTO.getPosterPath().startsWith("https")) {
            byte[] imageBytes = restTemplate.getForObject(movieDTO.getPosterPath(), byte[].class);
            saveImage(imageBytes, filename);
            // 직접 이미지 업로드한 경우
        } else if (movieDTO.getImage() != null && !movieDTO.getImage().isEmpty()) {
            saveImage(movieDTO.getImage().getBytes(), filename);
        }
    }


    // 이미지 저장
    public void saveImage(byte[] imageBytes, String filename) throws IOException {
        Path path = Paths.get(uploadPath, filename);

        // 파일 저장
        Files.write(path, imageBytes);

        // 썸네일 생성
        String contentType = Files.probeContentType(path);
        if (contentType != null && contentType.startsWith("image")) {
            File thumbnailFile = new File(uploadPath, "s_" + filename);
            Thumbnailator.createThumbnail(path.toFile(), thumbnailFile, 200, 200);
            log.info("thumbnailFile: {}", thumbnailFile);
        }
    }




    // 삭제
    @Override
    public void remove(long movieId) {
        movieRepository.deleteById(movieId);
        MovieEntity movieEntity = movieRepository.findById(movieId).orElseThrow();
    }




    // 상세 조회
    @Override
    public MovieDTO getMovieById(long movieId) {
        Optional<MovieEntity> optionalMovieEntity = movieRepository.findById(movieId);
        MovieEntity movieEntity = optionalMovieEntity.orElseThrow();
        MovieDTO movieDTO = MovieEntity.toDTO(movieEntity);
        return movieDTO;
    }


    // 제목으로 상세조회
    @Override
    public MovieDTO getMovieByTitle(String title) {
        Optional<MovieEntity> optionalMovieEntity = movieRepository.findByTitle(title);
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

    // 상영중인 영화 전체 조회
    @Override
    public List<MovieDTO> getScreeningPeriodAllMovies() {
        List<MovieEntity> movieEntityList = movieRepository.findAll();
        LocalDate now = LocalDate.now();

        List<MovieDTO> movieDTOList = new ArrayList<>();
        for (MovieEntity movieEntity : movieEntityList) {


            if (!now.isBefore(movieEntity.getStartAt()) && !now.isAfter(movieEntity.getEndAt())) {
                movieDTOList.add(MovieEntity.toDTO(movieEntity));
            }
        }
        return movieDTOList;
    }




//    // 제목 키워드로 조회
//    @Override
//    public List<MovieDTO> getMovie(String keyWord) {
//        List<MovieEntity> movieEntityList = movieRepository.findByTitleContaining(keyWord);
//
//        List<MovieDTO> movieDTOList = new ArrayList<>();
//        for (MovieEntity movieEntity : movieEntityList) {
//            movieDTOList.add(MovieEntity.toDTO(movieEntity));
//        }
//        return movieDTOList;
//    }
//
//
//
//    // 장르로 조회
//    @Override
//    public List<MovieDTO> findByGenre(String genre) {
//        List<MovieEntity> movieEntityList = movieRepository.findByGenre(genre);
//
//        List<MovieDTO> movieDTOList = new ArrayList<>();
//        for (MovieEntity movieEntity : movieEntityList) {
//            movieDTOList.add(MovieEntity.toDTO(movieEntity));
//        }
//        return movieDTOList;
//    }
//
//
//    // 관람등급으로 조회
//    @Override
//    public List<MovieDTO> findByRating(Rating rating) {
//        List<MovieEntity> movieEntityList = movieRepository.findByRating(rating);
//
//        List<MovieDTO> movieDTOList = new ArrayList<>();
//
//        for (MovieEntity movieEntity : movieEntityList) {
//            movieDTOList.add(MovieEntity.toDTO(movieEntity));
//        }
//
//        return movieDTOList;
//    }
//
//    @Override
//    public MovieResponseDTO<MovieDTO> getList(MovieRequestDTO movieRequestDTO) {
//        String[] types = movieRequestDTO.getTypes();
//        String keyword = movieRequestDTO.getKeyword();
////        movieRepository
//
//
//        return null;
//    }



}
