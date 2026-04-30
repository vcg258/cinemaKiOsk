package com.example.cinemakiosk.service;

import com.example.cinemakiosk.domain.MemberEntity;
import com.example.cinemakiosk.domain.MovieEntity;
import com.example.cinemakiosk.domain.ScheduleEntity;
import com.example.cinemakiosk.domain.enums.Rating;
import com.example.cinemakiosk.dto.MemberDTO;
import com.example.cinemakiosk.dto.MovieDTO;
import com.example.cinemakiosk.dto.MovieRequestDTO;
import com.example.cinemakiosk.dto.MovieResponseDTO;
import com.example.cinemakiosk.dto.ScheduleDTO;
import com.example.cinemakiosk.repository.MovieRepository;
import com.example.cinemakiosk.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import java.util.*;

@Service
@RequiredArgsConstructor
@Log4j2
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;
    private final ScheduleRepository scheduleRepository;
    private final RestTemplate restTemplate;


    // 이미지 저장 경로
    @Value("${my.upload.path}")
    private String uploadPath;


    /**
     * 영화 등록
     *
     * @param movieDTO 영화 정보
     */
    @Override
    public void insertMovie(MovieDTO movieDTO) {
        log.info("movieDTO: {} ", movieDTO);

        // 영화 정보 저장
        MovieEntity movieEntity = movieRepository.save(MovieDTO.toEntity(movieDTO));
        String filename = movieEntity.getMovieId() + ".jpg";  // movieId를 파일명으로

//        영화 이미지 저장
//        saveImageFromDTO(movieDTO, filename);
    }


    /**
     * 영화 수정
     *
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
//
////         3. 새 이미지가 있을 때만 처리
//        MultipartFile file1 = movieDTO.getImage();
//        String file2 = movieDTO.getPosterPath();
//
//        if ((file1 != null && !file1.isEmpty()) || (file2 != null && !file2.isEmpty())) {
//            String filename = movieDTO.getMovieId() + ".jpg";  // movieId로 파일명
//
////            // 기존 이미지 삭제
////           Path oldPath = Paths.get(uploadPath, filename);
////            Path oldThumbPath = Paths.get(uploadPath, "s_" + filename);
////            try {
////                Files.deleteIfExists(oldPath);
////                Files.deleteIfExists(oldThumbPath);
////            } catch (IOException e) {
////                log.warn("기존 이미지 삭제 실패");
////            }
//
//            // 영화 이미지 저장
//            try {
//                saveImageFromDTO(movieDTO, filename);
//            } catch (IllegalStateException e) {
//                throw e;
//            }
//        }
    }


    /**
     * 영화 삭제
     *
     * @param movieId 영화 PK
     */
    @Override
    public void remove(long movieId) {
        movieRepository.findById(movieId)
                .orElseThrow(() -> new NoSuchElementException("movieId를 찾을 수 없습니다"));

        movieRepository.deleteById(movieId);
    }


    /**
     * 상세 조회
     *
     * @param movieId
     * @return
     */
    @Override
    public MovieDTO getMovieById(Long movieId) {
        // 메시지 추가
        MovieEntity optionalMovieEntity = movieRepository.findById(movieId)
                .orElseThrow(() -> new NoSuchElementException("movieId를 찾을 수 없습니다"));
        MovieDTO movieDTO = MovieEntity.toDTO(optionalMovieEntity);
        return movieDTO;
    }


    // 제목으로 상세조회
    @Override
    public MovieDTO getMovieByTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title이 null입니다.");
        }

        Optional<MovieEntity> optionalMovieEntity = movieRepository.findByTitle(title);

        MovieEntity movieEntity = optionalMovieEntity
                .orElseThrow(() -> new NoSuchElementException("title을 찾을 수 없습니다: " + title));
        MovieDTO movieDTO = MovieEntity.toDTO(movieEntity);
        return movieDTO;
    }


    /**
     * 전체 영화 조회
     *
     * @return 현재 db에 저장된 모든 영화
     */
    @Override
    public List<MovieDTO> getAllMovies() {
        List<MovieEntity> movieEntityList = movieRepository.findAll();

//        // 영화 목록이 없을 때
//        if (movieEntityList.isEmpty()) {
//            throw new NoSuchElementException("등록된 영화가 없습니다.");
//        }
        // 그냥 빈 리스트 반환하게 둠


        List<MovieDTO> movieDTOList = new ArrayList<>();
        for (MovieEntity movieEntity : movieEntityList) {
            movieDTOList.add(MovieEntity.toDTO(movieEntity));
        }
        return movieDTOList;
    }

    /**
     * 상영종료처리
     * movieId를 입력하면, Schedule을 조회해 지나간 상영 시간중 가장 가까운 상영 시간을 end_at 시간으로 삼는다.
     *
     * @param movieId
     */
    @Override
    public void modifyEndAt(long movieId) {
        MovieEntity movieEntity = movieRepository.findById(movieId)
                .orElseThrow(() -> new NoSuchElementException("movieId를 찾을 수 없습니다"));

        // 현재시간
        LocalDateTime now = LocalDateTime.now();

        // movieId로 해당하는 스케쥴 리스트 가져오기
        List<ScheduleEntity> ScheduleEntityList = scheduleRepository.findByMovieEntity_MovieId(movieId);
        if (ScheduleEntityList.isEmpty()) {
            throw new NoSuchElementException("해당 영화에 스케쥴이 없습니다");
        }
        // 지나간 상영 시간중 가장 가까운 상영 시간을 가져옴
        LocalDateTime endAt = ScheduleEntityList.stream()
                .map(s -> s.getEndAt())
                .filter(dt -> !dt.isAfter(now))  // 미래 제외
                .max(Comparator.naturalOrder())  // 가장 가까운 과거
                .orElse(null);


        log.info(endAt);

        movieEntity.setEndAt(endAt);
        movieRepository.save(movieEntity);

    }


    /**
     * 해당날짜에 스케쥴이 있는 영화만 조회 (고객용)
     *
     * @return 현재 상영중인 영화
     */
    @Override
    public List<MovieDTO> getScreeningPeriodAllMovies() {
        List<MovieEntity> movieEntityList = movieRepository.findAll();
        log.info(movieEntityList);
        LocalDate now = LocalDate.now();

        List<MovieDTO> movieDTOList = new ArrayList<>();
        for (MovieEntity movieEntity : movieEntityList) {
            // movieId로 영화 상영 스케쥴 확인

            List<ScheduleEntity> byMovieEntityMovieId = scheduleRepository.findByMovieEntity_MovieId(movieEntity.getMovieId());

            for (ScheduleEntity scheduleEntity : byMovieEntityMovieId) {
                if (scheduleEntity.getStartAt().toLocalDate().isEqual(now) && scheduleEntity.isActivation()) {
                    // 오늘 날짜에 해당하는 시작 스케쥴이 있고 활성화 상태라면 list에 추가
                    movieDTOList.add(MovieEntity.toDTO(movieEntity));
                    break;
                }
            }
        }
        return movieDTOList;
    }

    /**
     * 10페이지씩 페이징 처리 (로그형식 전체)
     *
     * @param page 몇번째 페이지 부터 정할 변수
     * @return 페이징 결과 1페이지 일경우 1 ~ 10번 까지
     */
    @Override
    public Page<MovieDTO> getMoviePage(int page) {
        Pageable pageable = PageRequest.of(page - 1, 10, Sort.by("movieId").descending());
        Page<MovieEntity> entityPage = movieRepository.findAll(pageable);
        return entityPage.map(MovieEntity::toDTO);
    }


//
//    /**
//     * 상영중(상영기간중)인 영화 조회
//     * @return 현재 상영중인 영화
//     */
//    @Override
//    public List<MovieDTO> getScreeningPeriodAllMovies() {
//        List<MovieEntity> movieEntityList = movieRepository.findAll();
//        LocalDate now = LocalDate.now();
//
//
//
//        List<MovieDTO> movieDTOList = new ArrayList<>();
//        for (MovieEntity movieEntity : movieEntityList) {
//            if (!now.isBefore(movieEntity.getStartAt()) && !now.isAfter(movieEntity.getEndAt())) {
//                movieDTOList.add(MovieEntity.toDTO(movieEntity));
//            }
//        }
//
//        // 상영중인 영화가 없을 때
//        if (movieDTOList.isEmpty()) {
//            throw new NoSuchElementException("현재 상영중인 영화가 없습니다.");
//        }
//
//        return movieDTOList;
//    }
//

//
//        // 이미지 저장 필터
//        private void saveImageFromDTO (MovieDTO movieDTO, String filename){
//            // TMDB로 등록한 경우
//            if (movieDTO.getPosterPath() != null && movieDTO.getPosterPath().startsWith("https")) {
//
//                byte[] imageBytes = restTemplate.getForObject(movieDTO.getPosterPath(), byte[].class);
//
//                if (imageBytes == null || imageBytes.length == 0) {
//                    throw new IllegalStateException("이미지 다운로드에 실패했습니다: " + movieDTO.getPosterPath());
//                }
//
//                saveImage(imageBytes, filename);
//
//                // 직접 이미지 업로드한 경우
//            } else if (movieDTO.getImage() != null && !movieDTO.getImage().isEmpty()) {
//                try {
//                    saveImage(movieDTO.getImage().getBytes(), filename);
//                } catch (IOException e) {
//                    throw new IllegalStateException("이미지 저장에 실패했습니다: " + filename, e);
//                }
//            }
//        }
//
//
//        // 이미지 저장
//        public void saveImage ( byte[] imageBytes, String filename){
//            Path path = Paths.get(uploadPath, filename);
//
//            // 이미지 파일 저장
//            try {
//                Files.write(path, imageBytes);
//            } catch (IOException e) {
//                throw new IllegalStateException(" 파일 저장에 실패했습니다: " + filename, e);
//            }
//
//            // 썸네일 생성
//            String contentType = null;
//            try {
//                contentType = Files.probeContentType(path);
//            } catch (IOException e) {
//                throw new IllegalStateException("파일 타입 확인에 실패했습니다: " + filename, e);
//            }
//            if (contentType != null && contentType.startsWith("image")) {
//                File thumbnailFile = new File(uploadPath, "s_" + filename);
//                try {
//                    Thumbnailator.createThumbnail(path.toFile(), thumbnailFile, 200, 200);
//                } catch (IOException e) {
//                    throw new IllegalStateException("썸네일 생성에 실패했습니다: " + filename, e);
//                }
//            }
//        }
//

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