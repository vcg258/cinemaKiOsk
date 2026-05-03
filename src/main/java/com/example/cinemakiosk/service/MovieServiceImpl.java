package com.example.cinemakiosk.service;

import com.example.cinemakiosk.domain.MovieEntity;
import com.example.cinemakiosk.domain.ScheduleEntity;
import com.example.cinemakiosk.dto.MovieDTO;
import com.example.cinemakiosk.repository.MovieRepository;
import com.example.cinemakiosk.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Log4j2
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;
    private final ScheduleRepository scheduleRepository;

    /**
     * 영화 등록
     *
     * @param movieDTO 영화 정보
     */
    @Override
    public void insertMovie(MovieDTO movieDTO) {
        // 영화 정보 저장
        MovieEntity movieEntity = movieRepository.save(MovieDTO.toEntity(movieDTO));
        log.info("저장된 DTO: {} ", movieEntity);
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
         * @param movieId
         * @return
         */
        @Override
        public MovieDTO getMovieById (Long movieId){
            // 메시지 추가
            MovieEntity optionalMovieEntity = movieRepository.findById(movieId)
                    .orElseThrow(() -> new NoSuchElementException("movieId를 찾을 수 없습니다"));
            MovieDTO movieDTO = MovieEntity.toDTO(optionalMovieEntity);
            return movieDTO;
        }


        // 제목으로 상세조회
        @Override
        public MovieDTO getMovieByTitle (String title){
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
         * @return 현재 db에 저장된 모든 영화
         */
        @Override
        public List<MovieDTO> getAllMovies () {
            List<MovieEntity> movieEntityList = movieRepository.findAll();
            List<MovieDTO> movieDTOList = new ArrayList<>();
            for (MovieEntity movieEntity : movieEntityList) {
                movieDTOList.add(MovieEntity.toDTO(movieEntity));
            }
            return movieDTOList;
        }

        /**
         * 상영종료처리
         * movieId를 입력하면, Schedule을 조회해 지나간 상영 시간중 가장 가까운 상영 시간을 end_at 시간으로 삼는다.
         * @param movieId
         */
        @Override
        public void modifyEndAt ( long movieId){
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
        public List<MovieDTO> getScreeningPeriodAllMovies () {
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
    }

