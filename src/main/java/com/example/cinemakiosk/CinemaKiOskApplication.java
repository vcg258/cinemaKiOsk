package com.example.cinemakiosk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class CinemaKiOskApplication {

    public static void main(String[] args) {
        SpringApplication.run(CinemaKiOskApplication.class, args);
    }

}
