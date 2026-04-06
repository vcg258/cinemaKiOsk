package com.example.cinemakiosk.service;

import com.example.cinemakiosk.config.TmdbConfig;
import com.example.cinemakiosk.dto.MovieDTO;
import com.example.cinemakiosk.dto.TmdbCreditsDTO;
import com.example.cinemakiosk.dto.TmdbMovieDTO;
import com.example.cinemakiosk.dto.TmdbSearchResponseDTO;
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



    // 인기 영화 목록 (검색없을 시 기본값)
    public List<TmdbMovieDTO> getPopularMovies(int page) {
        String url = tmdbConfig.getBaseUrl() + "/movie/popular"
                + "?api_key=" + tmdbConfig.getApiKey()
                + "&language=ko-KR"
                + "&page=" + page;

        TmdbSearchResponseDTO response = restTemplate.getForObject(url, TmdbSearchResponseDTO.class);

        if (response == null) return new ArrayList<>();

        response.getResults().forEach(movie ->
                movie.setPosterPath(tmdbConfig.getImageUrl() + movie.getPosterPath())
        );

        return response.getResults();
    }


    // 검색 목록
    // id, title, poster(image url)
    public List<TmdbMovieDTO> searchMovies(String title) {
        String url = tmdbConfig.getBaseUrl() + "/search/movie"
                + "?api_key=" + tmdbConfig.getApiKey()
                + "&query=" + title
                + "&language=ko-KR";

        TmdbSearchResponseDTO response = restTemplate.getForObject(url, TmdbSearchResponseDTO.class);

        if (response == null) return new ArrayList<>();

        // poster_path에 imageUrl 붙여주기
        for (TmdbMovieDTO movieDTO : response.getResults()) {
            log.info("moviePosterPath: " + movieDTO.getPosterPath());
            movieDTO.setPosterPath(tmdbConfig.getImageUrl() + movieDTO.getPosterPath());
        }

        return response.getResults();
    }


    // 선택한 영화 ID로 상세조회
    public MovieDTO searchMovieDetail(Long tmdbId) {
        // 상세조회
        String url = tmdbConfig.getBaseUrl() + "/movie/" + tmdbId
                + "?api_key=" + tmdbConfig.getApiKey()
                + "&language=ko-KR";
        TmdbMovieDTO detail = restTemplate.getForObject(url, TmdbMovieDTO.class);

        // 상세조회(배우, 감독)
        String creditsUrl = tmdbConfig.getBaseUrl() + "/movie/" + tmdbId
                + "/credits?api_key=" + tmdbConfig.getApiKey()
                + "&language=ko-KR";
        TmdbCreditsDTO credits = restTemplate.getForObject(creditsUrl, TmdbCreditsDTO.class);

        // 감독 추출
        String director = credits.getCrew().stream()
                .filter(c -> "Director".equals(c.getJob()))
                .map(TmdbCreditsDTO.CrewDTO::getName)
                .findFirst()
                .orElse("");

        // 배우 상위 5명
        String actors = credits.getCast().stream()
                .limit(5)
                .map(TmdbCreditsDTO.CastDTO::getName)
                .collect(Collectors.joining(", "));

        // 장르
        String genre = detail.getGenres().stream()
                .map(TmdbMovieDTO.GenreDTO::getName)
                .collect(Collectors.joining(", "));

        try {
            downloadAndSavePoster(detail.getPosterPath(), detail.getTitle());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return MovieDTO.builder()
                .title(detail.getTitle())
                .description(detail.getOverview())
                .runtime(detail.getRuntime())
                .genre(genre)
                .director(director)
                .actors(actors)
                .build();
    }




    // 이미지 url 다운로드
    public void downloadAndSavePoster(String posterPath, String title) throws IOException {
        String imageUrl = tmdbConfig.getImageUrl() + posterPath;

        // URL에서 이미지 바이트 다운로드
        byte[] imageBytes = restTemplate.getForObject(imageUrl, byte[].class);

        // 영화 제목으로 파일명 설정
        String filename = title.replaceAll("[\\\\/:*?\"<>|]", "")  // 특수문자 제거
                .trim() + ".jpg";

        Path path = Paths.get(uploadPath, filename);
        try {
            Files.write(path, imageBytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String contentType = null;
        try {
            contentType = Files.probeContentType(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (contentType != null && contentType.startsWith("image")) {
            File thumbnailFile = new File(uploadPath, "s_" + filename);
            try {
                Thumbnailator.createThumbnail(path.toFile(), thumbnailFile, 200, 200);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }


    }




//    // 직접 업로드
//    saveImage(file.getBytes(), file.getOriginalFilename());
//
//    // TMDB
//    saveImage(imageBytes, filename);
}
