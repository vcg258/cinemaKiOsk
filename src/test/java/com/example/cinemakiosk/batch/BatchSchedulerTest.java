package com.example.cinemakiosk.batch;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@Log4j2
@SpringBootTest
class BatchSchedulerTest {

    @Autowired
    BatchScheduler batchScheduler;

    @Test
    void runJob() {
        batchScheduler.runJob(LocalDate.now().minusDays(1));

    }

    @Test
    void runJob2() {
        batchScheduler.runJob2();
    }


}