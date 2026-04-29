package com.example.cinemakiosk.service.ai;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.MalformedURLException;

@Log4j2
@SpringBootTest
class ETLServiceTest {
    @Autowired private ETLService etlService;

    @Test
    void etlFromJsonUrl() throws MalformedURLException {
        etlService.etlFromJsonUrl("https://jsonplaceholder.typicode.com/posts");
    }

    @Test
    void clearVectorStore() {
        etlService.clearVectorStore();
    }

    @Test
    void clearChatMemory() {
        etlService.clearChatMemory();
    }
}