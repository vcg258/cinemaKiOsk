package com.example.cinemakiosk.service;

import com.example.cinemakiosk.config.TmdbConfig;
import com.example.cinemakiosk.dto.MovieDTO;
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
    private final MovieService movieService;

    // 이미지 저장 경로
    @Value("${my.upload.path}")
    private String uploadPath;


    // 이미지 url 다운로드
    public void downloadAndSavePoster(String posterPath, String title) throws IOException {
        String imageUrl = tmdbConfig.getImageUrl() + posterPath;

        // URL에서 이미지 바이트 다운로드
        byte[] imageBytes = restTemplate.getForObject(imageUrl, byte[].class);

        // 영화 제목으로 파일명 설정
        String filename = title + ".jpg";

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




    // 검색 목록
    // (id, title, poster 만 사용)
    public List<TmdbMovieDTO> searchMovies(String title) {
        String url = tmdbConfig.getBaseUrl() + "/search/movie"
                + "?api_key=" + tmdbConfig.getApiKey()
                + "&query=" + title
                + "&language=ko-KR";
        TmdbSearchResponseDTO response = restTemplate.getForObject(url, TmdbSearchResponseDTO.class);
        return response != null ? response.getResults() : new ArrayList<>();
    }


    // 선택한 영화 ID로 상세조회 → 여기서 runtime, genres 옴
    public MovieDTO searchMovieDetail(Long tmdbId) {
        String url = tmdbConfig.getBaseUrl() + "/movie/" + tmdbId
                + "?api_key=" + tmdbConfig.getApiKey()
                + "&language=ko-KR";

        TmdbMovieDTO detail = restTemplate.getForObject(url, TmdbMovieDTO.class);
        try {
            downloadAndSavePoster(detail.getPosterPath(), detail.getTitle());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String genre = detail.getGenres().stream()
                .map(TmdbMovieDTO.GenreDTO::getName)
                .collect(Collectors.joining(", "));

        return MovieDTO.builder()
                .title(detail.getTitle())
                .description(detail.getOverview())
                .runtime(detail.getRuntime())
                .genre(genre)
                .build();
    }

//    // 직접 업로드
//    saveImage(file.getBytes(), file.getOriginalFilename());
//
//    // TMDB
//    saveImage(imageBytes, filename);
}
