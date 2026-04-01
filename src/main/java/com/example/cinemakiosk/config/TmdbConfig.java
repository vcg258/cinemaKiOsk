package com.example.cinemakiosk.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@ConfigurationProperties(prefix = "tmdb")
@Getter
@Setter
@EnableConfigurationProperties
public class TmdbConfig {
    private String apiKey;
    private String baseUrl;
    private String imageUrl;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
