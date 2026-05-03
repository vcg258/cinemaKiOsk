package com.example.cinemakiosk.service;

import com.example.cinemakiosk.config.TmdbConfig;
import com.example.cinemakiosk.domain.enums.Rating;
import com.example.cinemakiosk.dto.MovieDTO;
import com.example.cinemakiosk.dto.tmdb.TmdbCredits.CastDTO;
import com.example.cinemakiosk.dto.tmdb.TmdbCredits.CrewDTO;
import com.example.cinemakiosk.dto.tmdb.TmdbCredits.TmdbCreditsDTO;
import com.example.cinemakiosk.dto.tmdb.TmdbMovieDTO.GenreDTO;
import com.example.cinemakiosk.dto.tmdb.TmdbMovieDTO.TmdbMovieDTO;
import com.example.cinemakiosk.dto.tmdb.TmdbReleaseDatesDTO.ReleaseDate;
import com.example.cinemakiosk.dto.tmdb.TmdbReleaseDatesDTO.TmdbReleaseDatesDTO;
import com.example.cinemakiosk.dto.tmdb.TmdbSearchResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class TmdbServiceImpl implements TmdbService {
    private final TmdbConfig tmdbConfig;

    // 인기 영화 목록 수정
    public List<TmdbMovieDTO> getPopularMovies(int page) {

        // 페이지 번호 유효성 검사
        if (page < 1) {
            throw new IllegalArgumentException("페이지 번호는 1 이상이어야 합니다: " + page);
        }
        // 1. RestClient 초기화
        RestClient restClient = RestClient.builder()
                .baseUrl(tmdbConfig.getBaseUrl())
                .build();

        // 2. API 호출
        TmdbSearchResponseDTO response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/movie/popular")
                        .queryParam("api_key", tmdbConfig.getApiKey())
                        .queryParam("language", "ko-KR")
                        .queryParam("page", page)
                        .build())
                .retrieve()
                .body(TmdbSearchResponseDTO.class);


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

        // 1. RestClient 초기화
        RestClient restClient = RestClient.builder()
                .baseUrl(tmdbConfig.getBaseUrl())
                .build();

        // 2. API 호출
        TmdbSearchResponseDTO response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search/movie")
                        .queryParam("api_key", tmdbConfig.getApiKey())
                        .queryParam("query", title)
                        .queryParam("language", "ko-KR")
                        .build())
                .retrieve()
                .body(TmdbSearchResponseDTO.class);


        // API 응답 없을 때
        if (response == null || response.getResults() == null) {
            throw new NoSuchElementException("검색 결과를 가져올 수 없습니다: " + title);
        }

        // poster_path에 imageUrl 붙여주기
        for (TmdbMovieDTO movieDTO : response.getResults()) {
            log.info("moviePosterPath: {}", movieDTO.getPosterPath());
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

        // 1. RestClient 초기화
        RestClient restClient = RestClient.builder()
                .baseUrl(tmdbConfig.getBaseUrl())
                .build();

        // 2. API 호출
        TmdbMovieDTO detail = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/movie/" + tmdbId)
                        .queryParam("api_key", tmdbConfig.getApiKey())
                        .queryParam("language", "ko-KR")
                        .build())
                .retrieve()
                .body(TmdbMovieDTO.class);


        // 영화 정보 없을 때
        if (detail == null) {
            throw new NoSuchElementException("해당 영화를 찾을 수 없습니다. tmdbId=" + tmdbId);
        }

        TmdbCreditsDTO credits = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/movie/" + tmdbId + "/credits")
                        .queryParam("api_key", tmdbConfig.getApiKey())
                        .queryParam("language", "ko-KR")
                        .build())
                .retrieve()
                .body(TmdbCreditsDTO.class);

        // 크레딧 정보 없을 때
        if (credits == null) {
            throw new NoSuchElementException("해당 영화의 크레딧 정보를 찾을 수 없습니다. tmdbId: " + tmdbId);
        }

        // 감독 추출
        String director = credits.getCrew().stream()
                .filter(c -> "Director".equals(c.getJob()))
                .map(CrewDTO::getName)
                .findFirst()
                .orElse("");

        // 배우 상위 5명 가져오기
        String actors = credits.getCast().stream()
                .limit(5)
                .map(CastDTO::getName)
                .collect(Collectors.joining(", "));

        // 장르들 가져오기
        String genre = detail.getGenres().stream()
                .map(GenreDTO::getName)
                .collect(Collectors.joining(", "));

        // 한국 관람 등급 조회 (/movie/{id}/release_dates)
        Rating rating = fetchKoreanRating(tmdbId);
        log.info("searchMovieDetail... KR rating={}", rating);

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
                .rating(rating)
                .build();
    }

    /**
     * TMDB release_dates API로 관람 등급 조회
     * <p>
     * 조회 우선순위:
     * 1. KR(한국) certification — TMDB에 한국 개봉 데이터가 있는 경우
     * 2. US certification — KR 데이터 없을 때 미국 등급으로 대체 매핑
     * 3. 둘 다 없으면 ALL 기본값
     * <p>
     * KR 매핑:
     * "12"                      → TWELVE
     * "15"                      → FIFTEEN
     * "18" / "19" / "청소년관람불가" → NINETEEN
     * 그 외("All", "", ...)      → ALL
     * <p>
     * US 대체 매핑:
     * G           → ALL
     * PG          → TWELVE
     * PG-13       → FIFTEEN
     * R / NC-17   → NINETEEN
     *
     * @param tmdbId TMDB 영화 ID
     * @return Rating enum (조회 실패 시 ALL 기본값)
     */
    private Rating fetchKoreanRating(Long tmdbId) {
        try {
            RestClient restClient = RestClient.builder()
                    .baseUrl(tmdbConfig.getBaseUrl())
                    .build();

            TmdbReleaseDatesDTO response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/movie/" + tmdbId + "/release_dates")
                            .queryParam("api_key", tmdbConfig.getApiKey())
                            .build())
                    .retrieve()
                    .body(TmdbReleaseDatesDTO.class);

            if (response == null || response.getResults() == null) {
                return Rating.ALL;
            }

            // 1단계: KR 등급 우선 조회
            String krCert = response.getResults().stream()
                    .filter(r -> "KR".equals(r.getIso31661()))
                    .flatMap(r -> r.getReleaseDates().stream())
                    .map(ReleaseDate::getCertification)
                    .filter(c -> c != null && !c.isBlank())
                    .findFirst()
                    .orElse("");

            log.info("fetchKoreanRating... KR certification='{}'", krCert);

            if (!krCert.isBlank()) {
                return switch (krCert) {
                    case "12" -> Rating.TWELVE;
                    case "15" -> Rating.FIFTEEN;
                    case "18", "19", "청소년관람불가" -> Rating.NINETEEN;
                    default -> Rating.ALL;
                };
            }

            // 2단계: KR 데이터 없으면 US 등급으로 대체
            String usCert = response.getResults().stream()
                    .filter(r -> "US".equals(r.getIso31661()))
                    .flatMap(r -> r.getReleaseDates().stream())
                    .map(ReleaseDate::getCertification)
                    .filter(c -> c != null && !c.isBlank())
                    .findFirst()
                    .orElse("");

            log.info("fetchKoreanRating... KR 없음, US certification='{}'으로 대체", usCert);

            return switch (usCert) {
                case "G" -> Rating.ALL;
                case "PG" -> Rating.TWELVE;
                case "PG-13" -> Rating.FIFTEEN;
                case "R", "NC-17" -> Rating.NINETEEN;
                default -> Rating.ALL;
            };

        } catch (Exception e) {
            log.warn("fetchKoreanRating... 등급 조회 실패, 기본값 ALL 사용: {}", e.getMessage());
            return Rating.ALL;
        }
    }
}