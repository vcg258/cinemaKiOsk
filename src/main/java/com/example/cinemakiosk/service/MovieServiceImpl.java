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
import java.util.NoSuchElementException;
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


    /**
     * 영화 등록
     * @param movieDTO 영화 정보
     */
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
            throw new IllegalStateException("이미지 저장에 실패했습니다: " + filename, e);
        }
    }


    /**
     * 영화 수정
     * @param movieDTO
     */
    @Override
    public void modify(MovieDTO movieDTO) {

        // movieId 유효성 검사
        if (movieDTO.getMovieId() == null) {
            throw new IllegalArgumentException("movieId가 null입니다.");
        }

        // 1. 기존 데이터 들고옴
        MovieEntity movieEntity = movieRepository.findById(movieDTO.getMovieId())
                .orElseThrow(() -> new NoSuchElementException("movieId를 찾을 수 없습니다"));

        // 2. 전체 수정
        movieEntity.update(movieDTO);
        movieRepository.save(movieEntity);

        // 3. 새 이미지가 있을 때만 처리
        MultipartFile file1 = movieDTO.getImage();
        String file2 = movieDTO.getPosterPath();

        if ((file1 != null && !file1.isEmpty()) || (file2 != null && !file2.isEmpty())) {
            String filename = movieDTO.getMovieId() + ".jpg";  // movieId로 파일명

//            // 기존 이미지 삭제
//            Path oldPath = Paths.get(uploadPath, filename);
//            Path oldThumbPath = Paths.get(uploadPath, "s_" + filename);
//            try {
//                Files.deleteIfExists(oldPath);
//                Files.deleteIfExists(oldThumbPath);
//            } catch (IOException e) {
//                log.warn("기존 이미지 삭제 실패");
//            }

            // 영화 이미지 저장
            try {
                saveImageFromDTO(movieDTO, filename);
            } catch (IOException e) {
                throw new IllegalStateException("이미지 저장에 실패했습니다: " + filename, e);
            }
        }
    }


    // 이미지 저장 필터
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
    public void saveImage(byte[] imageBytes, String filename) {
        Path path = Paths.get(uploadPath, filename);

        // 이미지 파일 저장
        try {
            Files.write(path, imageBytes);
        } catch (IOException e) {
            throw new IllegalStateException(" 파일 저장에 실패했습니다: " + filename, e);
        }

        // 썸네일 생성
        String contentType = null;
        try {
            contentType = Files.probeContentType(path);
        } catch (IOException e) {
            throw new IllegalStateException("파일 타입 확인에 실패했습니다: " + filename, e);
        }
        if (contentType != null && contentType.startsWith("image")) {
            File thumbnailFile = new File(uploadPath, "s_" + filename);
            try {
                Thumbnailator.createThumbnail(path.toFile(), thumbnailFile, 200, 200);
            } catch (IOException e) {
                throw new IllegalStateException("썸네일 생성에 실패했습니다: " + filename, e);
            }
        }
    }


    /**
     * 영화 삭제
     * @param movieId 영화 PK
     */
    @Override
    public void remove(long movieId) {
        MovieEntity movieEntity = movieRepository.findById(movieId)
                .orElseThrow(() -> new NoSuchElementException("movieId를 찾을 수 없습니다"));
        String filename = movieEntity.getMovieId() + ".jpg";  // movieId로 파일

        movieRepository.deleteById(movieId);
    }


    // 상세 조회
    @Override
    public MovieDTO getMovieById(long movieId) {
        // [추가] 메시지 추가
        MovieEntity optionalMovieEntity = movieRepository.findById(movieId)
                .orElseThrow(() -> new NoSuchElementException("movieId를 찾을 수 없습니다"));
        MovieDTO movieDTO = MovieEntity.toDTO(optionalMovieEntity);
        return movieDTO;
    }


    // 제목으로 상세조회
    @Override
    public MovieDTO getMovieByTitle(String title) {
        // [추가] 검색어 유효성 검사
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title이 null입니다.");
        }

        Optional<MovieEntity> optionalMovieEntity = movieRepository.findByTitle(title);
        // [추가] 메시지 추가
        MovieEntity movieEntity = optionalMovieEntity
                .orElseThrow(() -> new NoSuchElementException("title을 찾을 수 없습니다: " + title));
        MovieDTO movieDTO = MovieEntity.toDTO(movieEntity);
        return movieDTO;
    }


    /**
     * 전체 영화 조회
     * @return 현재 db에 저장된 모든 영화
     */
    @Override
    public List<MovieDTO> getAllMovies() {
        List<MovieEntity> movieEntityList = movieRepository.findAll();

        // [추가] 영화 목록이 없을 때
        if (movieEntityList.isEmpty()) {
            throw new NoSuchElementException("등록된 영화가 없습니다.");
        }

        List<MovieDTO> movieDTOList = new ArrayList<>();
        for (MovieEntity movieEntity : movieEntityList) {
            movieDTOList.add(MovieEntity.toDTO(movieEntity));
        }
        return movieDTOList;
    }


    /**
     * 상영중(상영기간중)인 영화 조회
     * @return 현재 상영중인 영화
     */
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

        // [추가] 상영중인 영화가 없을 때
        if (movieDTOList.isEmpty()) {
            throw new NoSuchElementException("현재 상영중인 영화가 없습니다.");
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
