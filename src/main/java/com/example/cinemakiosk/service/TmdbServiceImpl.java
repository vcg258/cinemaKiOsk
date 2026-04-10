package com.example.cinemakiosk.service;

import com.example.cinemakiosk.config.TmdbConfig;
import com.example.cinemakiosk.domain.enums.Rating;
import com.example.cinemakiosk.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnailator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class TmdbServiceImpl implements TmdbService {

    private final TmdbConfig tmdbConfig;
    private final RestTemplate restTemplate;

    // 이미지 저장 경로
    @Value("${my.upload.path}")
    private String uploadPath;


    // 인기 영화 목록
    public List<TmdbMovieDTO> getPopularMovies(int page) {
        // 페이지 번호 유효성 검사
        if (page < 1) {
            throw new IllegalArgumentException("페이지 번호는 1 이상이어야 합니다: " + page);
        }

        String url = tmdbConfig.getBaseUrl() + "/movie/popular"
                + "?api_key=" + tmdbConfig.getApiKey()
                + "&language=ko-KR"
                + "&page=" + page;

        TmdbSearchResponseDTO response = restTemplate.getForObject(url, TmdbSearchResponseDTO.class);

        // API 응답 없을 때
        if (response == null || response.getResults() == null) {
            throw new NoSuchElementException("API 응답없음");
        }

        response.getResults().forEach(movie ->
                movie.setPosterPath(tmdbConfig.getImageUrl() + movie.getPosterPath())
        );

        return response.getResults();
    }


    // 검색 목록
    // id, title, poster(image url)
    public List<TmdbMovieDTO> searchMovies(String title) {
        // 검색어 유효성 검사
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("제대로된 검색어를 입력해주세요.");
        }

        String url = tmdbConfig.getBaseUrl() + "/search/movie"
                + "?api_key=" + tmdbConfig.getApiKey()
                + "&query=" + title
                + "&language=ko-KR";

        TmdbSearchResponseDTO response = restTemplate.getForObject(url, TmdbSearchResponseDTO.class);

        // API 응답 없을 때
        if (response == null || response.getResults() == null) {
            throw new NoSuchElementException("검색 결과를 가져올 수 없습니다: " + title);
        }

        // poster_path에 imageUrl 붙여주기
        for (TmdbMovieDTO movieDTO : response.getResults()) {
            log.info("moviePosterPath: " + movieDTO.getPosterPath());
            movieDTO.setPosterPath(tmdbConfig.getImageUrl() + movieDTO.getPosterPath());
        }

        return response.getResults();
    }


    // 선택한 영화 ID로 상세조회
    public MovieDTO searchMovieDetail(Long tmdbId) {
        // tmdbId 유효성 검사
        if (tmdbId == null) {
            throw new IllegalArgumentException("tmdbId가 null입니다.");
        }

        // 상세조회
        String url = tmdbConfig.getBaseUrl() + "/movie/" + tmdbId
                + "?api_key=" + tmdbConfig.getApiKey()
                + "&language=ko-KR";
        TmdbMovieDTO detail = restTemplate.getForObject(url, TmdbMovieDTO.class);

        // 영화 정보 없을 때
        if (detail == null) {
            throw new NoSuchElementException("해당 영화를 찾을 수 없습니다. tmdbId=" + tmdbId);
        }

        // 상세조회(배우, 감독)
        String creditsUrl = tmdbConfig.getBaseUrl() + "/movie/" + tmdbId
                + "/credits?api_key=" + tmdbConfig.getApiKey()
                + "&language=ko-KR";
        TmdbCreditsDTO credits = restTemplate.getForObject(creditsUrl, TmdbCreditsDTO.class);

        // 크레딧 정보 없을 때
        if (credits == null) {
            throw new NoSuchElementException("해당 영화의 크레딧 정보를 찾을 수 없습니다. tmdbId: " + tmdbId);
        }

        // 감독 추출
        String director = credits.getCrew().stream()
                .filter(c -> "Director".equals(c.getJob()))
                .map(TmdbCreditsDTO.CrewDTO::getName)
                .findFirst()
                .orElse("");

        // 배우 상위 5명 가져오기
        String actors = credits.getCast().stream()
                .limit(5)
                .map(TmdbCreditsDTO.CastDTO::getName)
                .collect(Collectors.joining(", "));

        // 장르들 가져오기
        String genre = detail.getGenres().stream()
                .map(TmdbMovieDTO.GenreDTO::getName)
                .collect(Collectors.joining(", "));

        // 포스터 다운로드 및 저장
//        downloadAndSavePoster(detail.getPosterPath(), detail.getTitle());

        String posterFullUrl = detail.getPosterPath() != null
                ? tmdbConfig.getImageUrl() + detail.getPosterPath()
                : null;

        return MovieDTO.builder()
                .title(detail.getTitle())
                .description(detail.getOverview())
                .runtime(detail.getRuntime())
                .genre(genre)
                .director(director)
                .actors(actors)
                .posterPath(posterFullUrl)
                .build();
    }





    /**
     * TMDB release_dates API로 한국(KR) 관람 등급 조회
     * certification 값 → Rating enum 변환
     *
     * TMDB KR certification 규칙:
     *   "All" / ""  → ALL (전체관람가)
     *   "12"        → TWELVE (12세이상)
     *   "15"        → FIFTEEN (15세이상)
     *   "18" / "19" → NINETEEN (청소년관람불가)
     *
     * @param tmdbId TMDB 영화 ID
     * @return Rating enum (조회 실패 시 ALL 기본값)
     */
    private Rating fetchKoreanRating(Long tmdbId) {
        try {
            String url = tmdbConfig.getBaseUrl() + "/movie/" + tmdbId
                    + "/release_dates?api_key=" + tmdbConfig.getApiKey();

            TmdbReleaseDatesDTO response = restTemplate.getForObject(url, TmdbReleaseDatesDTO.class);

            if (response == null || response.getResults() == null) {
                log.warn("fetchKoreanRating... release_dates 응답 없음, 기본값 ALL 사용");
                return Rating.ALL;
            }

            // KR 항목 찾기
            String certification = response.getResults().stream()
                    .filter(r -> "KR".equals(r.getIso31661()))
                    .flatMap(r -> r.getReleaseDates().stream())
                    .map(TmdbReleaseDatesDTO.ReleaseDate::getCertification)
                    .filter(c -> c != null && !c.isBlank())
                    .findFirst()
                    .orElse("");

            log.info("fetchKoreanRating... KR certification='{}'", certification);

            // certification → Rating enum 변환
            return switch (certification) {
                case "12"       -> Rating.TWELVE;
                case "15"       -> Rating.FIFTEEN;
                case "18", "19" -> Rating.NINETEEN;
                default         -> Rating.ALL; // "All", "", 기타 → 전체관람가
            };

        } catch (Exception e) {
            // 등급 조회 실패해도 상세 조회 자체는 성공해야 하므로 기본값 반환
            log.warn("fetchKoreanRating... 등급 조회 실패, 기본값 ALL 사용: {}", e.getMessage());
            return Rating.ALL;
        }
    }




    // 이미지 url 다운로드
//    public void downloadAndSavePoster(String posterPath, String title) {
//        // 입력값 유효성 검사
//        if (posterPath == null || posterPath.isBlank()) {
//            throw new IllegalArgumentException("포스터 경로가 없습니다.");
//        }
//        if (title == null || title.isBlank()) {
//            throw new IllegalArgumentException("영화 제목이 없습니다.");
//        }
//
//        String imageUrl = tmdbConfig.getImageUrl() + posterPath;
//
//        // URL에서 이미지 바이트 다운로드
//        byte[] imageBytes = restTemplate.getForObject(imageUrl, byte[].class);
//
//        // 다운로드 실패 시
//        if (imageBytes == null || imageBytes.length == 0) {
//            throw new IllegalStateException("포스터 이미지 다운로드에 실패했습니다: " + imageUrl);
//        }
//
//        // 영화 제목으로 파일명 설정 (특수문자 제거)
//        String filename = title.replaceAll("[\\\\/:*?\"<>|]", "").trim() + ".jpg";
//        Path path = Paths.get(uploadPath, filename);
//
//        // 이미지 파일 저장
//        try {
//            Files.write(path, imageBytes);
//        } catch (IOException e) {
//            throw new IllegalStateException("포스터 파일 저장에 실패했습니다: " + filename, e);
//        }
//
//        // 썸네일 생성
//        String contentType = null;
//        try {
//            contentType = Files.probeContentType(path);
//        } catch (IOException e) {
//            throw new IllegalStateException("파일 타입 확인에 실패했습니다: " + filename, e);
//        }
//        if (contentType != null && contentType.startsWith("image")) {
//            File thumbnailFile = new File(uploadPath, "s_" + filename);
//            try {
//                Thumbnailator.createThumbnail(path.toFile(), thumbnailFile, 200, 200);
//            } catch (IOException e) {
//                throw new IllegalStateException("썸네일 생성에 실패했습니다: " + filename, e);
//            }
//        }
//    }
}