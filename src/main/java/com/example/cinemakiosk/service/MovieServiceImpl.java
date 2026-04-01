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
        log.info("rating2: {}", movieDTO.getRating());

        // -------- 영화 정보 저장 -------- //

        MovieEntity movieEntity = MovieDTO.toEntity(movieDTO);
        log.info("rating3: {}", movieEntity.getRating());
        movieRepository.save(movieEntity);

        // -------- 영화 이미지 저장 -------- //
        MultipartFile file = movieDTO.getImage();
        String originalFilename = file.getOriginalFilename();

        try {
            saveImage(file.getBytes(), originalFilename);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

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

    // 수정
    @Override
    public void modify(MovieDTO movieDTO) {

        // 1. 기존 데이터를 들고옴
        Optional<MovieEntity> optionalMovie = movieRepository.findById(movieDTO.getMovieId());
        MovieEntity movieEntity = optionalMovie.orElseThrow();

        // 전체 수정
        movieEntity.update(movieDTO);
        movieRepository.save(movieEntity);

        // 3. 첨부파일 처리 (새 이미지가 있을 때만)
        MultipartFile file = movieDTO.getImage();

        if (file != null && !file.isEmpty()) {

            // 1) 기존 이미지 삭제 (원본 + 썸네일)
            String oldFilename = movieDTO.getImage().getOriginalFilename(); // 기존 파일명 컬럼

            if (oldFilename != null && !oldFilename.isBlank()) {
                // 원본 삭제
                Path oldPath = Paths.get(uploadPath, oldFilename);
                try { Files.deleteIfExists(oldPath); } catch (IOException e) { log.warn("원본 삭제 실패: {}", oldPath); }

                // 썸네일 삭제
                Path oldThumbPath = Paths.get(uploadPath, "s_" + oldFilename);
                try { Files.deleteIfExists(oldThumbPath); } catch (IOException e) { log.warn("썸네일 삭제 실패: {}", oldThumbPath); }
            }

            // 2) 새 이미지 저장
            String originalFilename = file.getOriginalFilename();
            Path newPath = Paths.get(uploadPath, originalFilename);

            try {
                file.transferTo(newPath);

                // 썸네일 생성
                String contentType = Files.probeContentType(newPath);
                if (contentType != null && contentType.startsWith("image")) {
                    File thumbnailFile = new File(uploadPath, "s_" + originalFilename);
                    Thumbnailator.createThumbnail(newPath.toFile(), thumbnailFile, 200, 200);
                }

                // 3) DB에 새 파일명 업데이트
//                movieEntity.setImageName(originalFilename);
//                movieRepository.save(movieEntity);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }











    // 상세 조회
    @Override
    public MovieDTO getMovieById(long movieId) {
        Optional<MovieEntity> optionalMovieEntity = movieRepository.findById(movieId);
        MovieEntity movieEntity = optionalMovieEntity.orElseThrow();
        MovieDTO movieDTO = MovieEntity.toDTO(movieEntity);
        return movieDTO;
    }

    @Override
    public MovieDTO getMovieByTitle(String title) {
        return null;
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
        LocalDateTime now = LocalDateTime.now();

        List<MovieDTO> movieDTOList = new ArrayList<>();
        for (MovieEntity movieEntity : movieEntityList) {

            if(now.isAfter(movieEntity.getStartAt()) && now.isBefore(movieEntity.getEndAt())) {
                movieDTOList.add(MovieEntity.toDTO(movieEntity));
            }
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

    @Override
    public MovieResponseDTO<MovieDTO> getList(MovieRequestDTO movieRequestDTO) {
        String[] types = movieRequestDTO.getTypes();
        String keyword = movieRequestDTO.getKeyword();
//        movieRepository


        return null;
    }



    // 삭제
    @Override
    public void remove(long movieId) {
        movieRepository.deleteById(movieId);
    }
}
