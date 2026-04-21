//package com.example.cinemakiosk.service;
//
//import com.example.cinemakiosk.config.TmdbConfig;
//import com.example.cinemakiosk.dto.MovieDTO;
//import com.example.cinemakiosk.dto.TmdbCredits.TmdbCreditsDTO;
//import com.example.cinemakiosk.dto.TmdbMovieDTO;
//import com.example.cinemakiosk.dto.TmdbSearchResponseDTO;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.log4j.Log4j2;
//import net.coobird.thumbnailator.Thumbnailator;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//import java.io.File;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Log4j2
//@Service
//@RequiredArgsConstructor
//public class TmdbServiceImpl2 implements TmdbService {
//
//    private final TmdbConfig tmdbConfig;
//    private final RestTemplate restTemplate;
//
//    // 이미지 저장 경로
//    @Value("${my.upload.path}")
//    private String uploadPath;
//
//
//
//    // 인기 영화 목록 (검색없을 시 기본값)
//    public List<TmdbMovieDTO> getPopularMovies(int page) {
//        String url = tmdbConfig.getBaseUrl() + "/movie/popular"
//                + "?api_key=" + tmdbConfig.getApiKey()
//                + "&language=ko-KR"
//                + "&page=" + page;
//
//        TmdbSearchResponseDTO response = restTemplate.getForObject(url, TmdbSearchResponseDTO.class);
//
//        if (response == null) return new ArrayList<>();
//
//        response.getResults().forEach(movie ->
//                movie.setPosterPath(tmdbConfig.getImageUrl() + movie.getPosterPath())
//        );
//
//        return response.getResults();
//    }
//
//
//    // 검색 목록
//    // id, title, poster(image url)
//    public List<TmdbMovieDTO> searchMovies(String title) {
//        String url = tmdbConfig.getBaseUrl() + "/search/movie"
//                + "?api_key=" + tmdbConfig.getApiKey()
//                + "&query=" + title
//                + "&language=ko-KR";
//
//        TmdbSearchResponseDTO response = restTemplate.getForObject(url, TmdbSearchResponseDTO.class);
//
//        if (response == null) return new ArrayList<>();
//
//        // poster_path에 imageUrl 붙여주기
//        for (TmdbMovieDTO movieDTO : response.getResults()) {
//            log.info("moviePosterPath: " + movieDTO.getPosterPath());
//            movieDTO.setPosterPath(tmdbConfig.getImageUrl() + movieDTO.getPosterPath());
//        }
//
//        return response.getResults();
//    }
//
//
//    // 선택한 영화 ID로 상세조회
//    public MovieDTO searchMovieDetail(Long tmdbId) {
//        // 상세조회
//        String url = tmdbConfig.getBaseUrl() + "/movie/" + tmdbId
//                + "?api_key=" + tmdbConfig.getApiKey()
//                + "&language=ko-KR";
//        TmdbMovieDTO detail = restTemplate.getForObject(url, TmdbMovieDTO.class);
//
//        // detail이 null이면 에러 대신 빈 DTO 반환
//        if (detail == null) {
//            log.warn("TMDB 영화 상세 조회 결과 없음: tmdbId={}", tmdbId);
//            return MovieDTO.builder().build();
//        }
//
//        // 상세조회(배우, 감독)
//        String creditsUrl = tmdbConfig.getBaseUrl() + "/movie/" + tmdbId
//                + "/credits?api_key=" + tmdbConfig.getApiKey()
//                + "&language=ko-KR";
//        TmdbCreditsDTO credits = restTemplate.getForObject(creditsUrl, TmdbCreditsDTO.class);
//
//        // 감독 추출 (credits null 또는 crew null 방어)
//        String director = "";
//        if (credits != null && credits.getCrew() != null) {
//            director = credits.getCrew().stream()
//                    .filter(c -> "Director".equals(c.getJob()))
//                    .map(TmdbCreditsDTO.CrewDTO::getName)
//                    .findFirst()
//                    .orElse("");
//        }
//
//        // 배우 상위 5명 (cast null 방어)
//        String actors = "";
//        if (credits != null && credits.getCast() != null) {
//            actors = credits.getCast().stream()
//                    .limit(5)
//                    .map(TmdbCreditsDTO.CastDTO::getName)
//                    .collect(Collectors.joining(", "));
//        }
//
//        // 장르 (genres null 방어)
//        String genre = "";
//        if (detail.getGenres() != null) {
//            genre = detail.getGenres().stream()
//                    .map(TmdbMovieDTO.GenreDTO::getName)
//                    .collect(Collectors.joining(", "));
//        }
//
//        // 포스터 다운로드 — 실패해도 500 내지 않고 로그만 남김
//        // (업로드 경로 미존재, 네트워크 오류 등 방어)
//        if (detail.getPosterPath() != null) {
//            try {
//                downloadAndSavePoster(detail.getPosterPath(), detail.getTitle());
//            } catch (Exception e) {
//                log.warn("포스터 다운로드 실패 (tmdbId={}): {}", tmdbId, e.getMessage());
//            }
//        }
//
//        // posterPath: 프론트에서 미리보기에 사용할 수 있도록 전체 이미지 URL 반환
//        String posterFullUrl = detail.getPosterPath() != null
//                ? tmdbConfig.getImageUrl() + detail.getPosterPath()
//                : null;
//
//        return MovieDTO.builder()
//                .title(detail.getTitle())
//                .description(detail.getOverview())
//                .runtime(detail.getRuntime())
//                .genre(genre)
//                .director(director)
//                .actors(actors)
//                .posterPath(posterFullUrl)   // 전체 이미지 URL 포함
//                .build();
//    }
//
//
//
//
//
//
//
//
//
//
//
//    // 이미지 url 다운로드
//    public void downloadAndSavePoster(String posterPath, String title) throws IOException {
//        String imageUrl = tmdbConfig.getImageUrl() + posterPath;
//
//        // URL에서 이미지 바이트 다운로드
//        byte[] imageBytes = restTemplate.getForObject(imageUrl, byte[].class);
//
//        // 다운로드 실패 시 (null 반환) 조용히 종료
//        if (imageBytes == null) {
//            log.warn("TMDB 이미지 바이트 null: url={}", imageUrl);
//            return;
//        }
//
//        // 업로드 디렉토리가 없으면 자동 생성
//        Path dirPath = Paths.get(uploadPath);
//        if (!Files.exists(dirPath)) {
//            Files.createDirectories(dirPath);
//            log.info("업로드 디렉토리 생성: {}", uploadPath);
//        }
//
//        // 영화 제목으로 파일명 설정 (특수문자 제거)
//        String filename = title.replaceAll("[\\\\/:*?\"<>|]", "")
//                .trim() + ".jpg";
//
//        Path path = Paths.get(uploadPath, filename);
//        Files.write(path, imageBytes);
//        log.info("포스터 저장 완료: {}", path);
//
//        // 썸네일 생성 (이미지 파일인 경우에만)
//        String contentType = Files.probeContentType(path);
//        if (contentType != null && contentType.startsWith("image")) {
//            File thumbnailFile = new File(uploadPath, "s_" + filename);
//            Thumbnailator.createThumbnail(path.toFile(), thumbnailFile, 200, 200);
//            log.info("썸네일 생성 완료: {}", thumbnailFile.getPath());
//        }
//    }
//
//
//
//
////    // 직접 업로드
////    saveImage(file.getBytes(), file.getOriginalFilename());
////
////    // TMDB
////    saveImage(imageBytes, filename);
//}
