package com.example.cinemakiosk.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    /**
     * CORS에서 8080포트를 막아주는걸 허용하는 설정
     * @param registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
       registry.addMapping("/api/**")
               .allowedOrigins("http://localhost:3000")
               .allowedMethods("GET", "POST", "PUT", "DELETE");
    }
}
