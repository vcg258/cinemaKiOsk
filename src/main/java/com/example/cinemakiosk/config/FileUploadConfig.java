package com.example.cinemakiosk.config;

import jakarta.servlet.MultipartConfigElement;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

import java.io.File;

@Configuration
public class FileUploadConfig {

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();

        // 현재 프로젝트의 루트 경로를 가져옴
        String projectPath = System.getProperty("user.dir");

        // 프로젝트 루트 아래의 'uploads' 폴더를 경로로 지정
        String uploadPath = projectPath + File.separator + "uploads";

        // 폴더가 없으면 생성
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // 업로드 임시 저장소 위치 설정
        factory.setLocation(uploadPath);

        // 최대 파일 크기 설정
        factory.setMaxFileSize(DataSize.ofMegabytes(10));
        factory.setMaxRequestSize(DataSize.ofMegabytes(10));

        return factory.createMultipartConfig();
    }
}
