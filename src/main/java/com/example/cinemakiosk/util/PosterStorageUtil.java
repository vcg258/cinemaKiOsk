package com.example.cinemakiosk.util;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.ZoneId;

@Log4j2
@Component
public class PosterStorageUtil {

    private static final int DOWNLOAD_TIMEOUT_MS = 30_000;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    /**
     * 직접 업로드 파일 저장
     * @param title 영화 제목
     * @param createAt DB create_at (yyyy-MM-dd)
     * @param file 업로드된 파일
     * @return 저장된 포스터 경로 (/uploads/파일명)
     */
    public String saveFromFile(String title, String createAt, MultipartFile file) throws IOException {
        String ext = extensionFromFilename(file.getOriginalFilename());
        return saveBuffer(title, createAt, file.getBytes(), ext);
    }

    /**
     * 외부 URL(TMDB 등) 이미지 다운로드 후 저장
     * @param title 영화 제목
     * @param createAt DB create_at (yyyy-MM-dd)
     * @param imageUrl 다운로드할 이미지 URL
     * @return 저장된 포스터 경로 (/uploads/파일명)
     */
    public String saveFromUrl(String title, String createAt, String imageUrl) throws IOException {
        byte[] buffer = downloadImageBuffer(imageUrl);
        String ext = extensionFromUrl(imageUrl);
        return saveBuffer(title, createAt, buffer, ext);
    }

    // 파일명에 들어가면 안되는 특수문자 -> '_', 공백 -> '_'
    private String sanitizeTitle(String title) {
        String trimmed = (title == null) ? "" : title.trim();
        if (trimmed.isEmpty()) return "movie";
        String sanitized = trimmed
                .replaceAll("[\\\\/:*?\"<>|]", "_")
                .replaceAll("\\s+", "_");
        return sanitized.isEmpty() ? "movie" : sanitized;
    }

    // 앞 10자(yyyy-MM-dd) 사용, 값이 없으면 KST 오늘 날짜
    private String normalizeCreateAt(String createAt) {
        String value = (createAt == null) ? "" : createAt.trim();
        if (value.length() >= 10) return value.substring(0, 10);
        return LocalDate.now(ZoneId.of("Asia/Seoul")).toString();
    }

    private String buildBaseFileName(String title, String createAt) {
        return sanitizeTitle(title) + "_" + normalizeCreateAt(createAt);
    }

    private String extensionFromFilename(String filename) {
        if (filename == null || filename.isEmpty()) return "jpg";
        int dot = filename.lastIndexOf('.');
        if (dot < 0 || dot == filename.length() - 1) return "jpg";
        String ext = filename.substring(dot + 1).toLowerCase();
        return ext.isEmpty() ? "jpg" : ext;
    }

    private String extensionFromUrl(String url) {
        try {
            return extensionFromFilename(new URL(url).getPath());
        } catch (Exception e) {
            return "jpg";
        }
    }

    // 동일 파일명이 있으면 영화명_날짜(1).ext, (2).ext ... 순으로 증가
    private Path resolveUniqueFilePath(Path dir, String baseName, String ext) {
        Path first = dir.resolve(baseName + "." + ext);
        if (!Files.exists(first)) return first;
        int n = 1;
        while (true) {
            Path candidate = dir.resolve(baseName + "(" + n + ")." + ext);
            if (!Files.exists(candidate)) return candidate;
            n++;
        }
    }

    private String saveBuffer(String title, String createAt, byte[] buffer, String ext) throws IOException {
        Path dir = Paths.get(uploadDir).toAbsolutePath();
        Files.createDirectories(dir);

        String baseName = buildBaseFileName(title, createAt);
        Path filePath = resolveUniqueFilePath(dir, baseName, ext);

        Files.write(filePath, buffer);
        log.info("포스터 저장 완료: {}", filePath);
        return "/uploads/" + filePath.getFileName().toString();
    }

    /**
     * 외부 URL 이미지 다운로드
     * @param imageUrl 다운로드할 이미지 URL
     */
    private byte[] downloadImageBuffer(String imageUrl) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(imageUrl).openConnection();
        conn.setConnectTimeout(DOWNLOAD_TIMEOUT_MS);
        conn.setReadTimeout(DOWNLOAD_TIMEOUT_MS);
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (compatible; CinemaKiosk/1.0)");
        conn.setRequestProperty("Accept", "image/avif,image/webp,image/apng,image/*,*/*;q=0.8");

        int status = conn.getResponseCode();
        if (status != HttpURLConnection.HTTP_OK) {
            conn.disconnect();
            throw new IOException("포스터 URL 다운로드 실패: HTTP " + status);
        }

        try (InputStream in = conn.getInputStream();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] buf = new byte[8192];
            int read;
            while ((read = in.read(buf)) != -1) out.write(buf, 0, read);
            return out.toByteArray();
        } finally {
            conn.disconnect();
        }
    }
}
