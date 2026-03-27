package com.example.cinemakiosk.mapper;

import com.example.cinemakiosk.domain.MovieEntity;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Log4j2
@SpringBootTest
class MovieMapperTest {

    @Autowired
    private MovieMapper movieMapper;

    @Test
    void findBySeveral() {
        List<MovieEntity> movieDTOList = movieMapper.findBySeveral("하", null, null);
        assertNotNull(movieDTOList);

        for (MovieEntity movieEntity : movieDTOList) {
            log.info(movieEntity);
        }
    }
}