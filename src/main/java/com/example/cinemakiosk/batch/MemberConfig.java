package com.example.cinemakiosk.batch;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Log4j2
@Configuration
@RequiredArgsConstructor
@EnableScheduling
public class MemberConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    @Qualifier("mariaDB")
    private final DataSource dataSource;
    private final MemberCleanupWriter memberCleanupWriter;

    @Bean
    public Job memberCleanupJob() {
        return new JobBuilder("memberCleanupJob", jobRepository)
                .start(memberCleanupStep())
                .build();
    }

    @Bean
    public Step memberCleanupStep() {
        return new StepBuilder("memberCleanupStep", jobRepository)
                .<String, String>chunk(100, transactionManager) // 100건씩 처리
                .reader(inactiveMemberReader())
                .writer(memberCleanupWriter)
                .build();
    }


    // read
    @Bean
    public JdbcCursorItemReader<String> inactiveMemberReader() {
        return new JdbcCursorItemReaderBuilder<String>()
                .name("inactiveMemberReader")
                .dataSource(dataSource)
                .sql("""
                    SELECT m.phone
                FROM member m
                WHERE m.create_at <= NOW() - INTERVAL 2 YEAR
                AND NOT EXISTS (
                    SELECT 1
                    FROM point_history p
                    WHERE p.phone = m.phone
                      AND p.create_at >= NOW() - INTERVAL 2 YEAR
                    )
                """)
                .rowMapper((rs, rowNum) -> rs.getString("phone"))
                .build();
    }
}
