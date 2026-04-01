package com.example.cinemakiosk.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public GroupedOpenApi restApi() {
        /* 경로에 /api가 포함된 컨트롤러의 경우에는 REST API로 인식 */
        return GroupedOpenApi.builder()
                .pathsToMatch("/api/**")
                .group("REST API")
                .build();
    }

    @Bean
    public GroupedOpenApi commonApi() {
        /* 경로에 /api가 포함안된 컨트롤러의 경우에는 COMMON API로 인식 */
        return GroupedOpenApi.builder()
                .pathsToMatch("/**/*")
                .pathsToExclude("/api/**/*") // 제외할 경로
                .group("COMMON API")
                .build();
    }
}


