package com.example.cinemakiosk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class CinemaKiOskApplication {

    public static void main(String[] args) {
        SpringApplication.run(CinemaKiOskApplication.class, args);
    }

}
