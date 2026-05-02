package com.example.cinemakiosk.batch;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jspecify.annotations.NonNull;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
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

    /**
     * 포인트 내역이 2년 이상 없는 회원 정리 -> 포인트 내역이 1달 이상 없는 회원 등급 강등 순서로 실행되는 Job
     */
    @Bean
    public Job memberCleanupJob() {
        return new JobBuilder("memberCleanupJob", jobRepository)
                .start(memberCleanupStep())
                .next(demoteStep())
                .build();
    }

    /**
     * 2년 이상 포인트 내역이 없는 회원의 phone을 DEL_ 형태로 마킹하는 Step
     */
    @Bean
    public Step memberCleanupStep() {
        return new StepBuilder("memberCleanupStep", jobRepository)
                .<String, String>chunk(100, transactionManager) // 100건씩 처리
                .reader(inactiveMemberReader())
                .writer(memberCleanupWriter)
                .build();
    }

    /**
     * 1년 이상 포인트 내역이 없는 NORMAL 등급 회원을 강등하는 Step
     */
    @Bean
    public Step demoteStep() {
        return new StepBuilder("demoteStep", jobRepository)
                .<String, String>chunk(100, transactionManager)
                .reader(inactiveDemoteReader())
                .writer(new ItemWriter<String>() {
                    @Override
                    public void write(@NonNull Chunk<? extends String> chunk) throws Exception {
                        memberCleanupWriter.demote(chunk);
                    }
                })
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

    // 강등 Reader 추가
    @Bean
    public JdbcCursorItemReader<String> inactiveDemoteReader() {
        return new JdbcCursorItemReaderBuilder<String>()
                .name("inactiveDemoteReader")
                .dataSource(dataSource)
                .sql("""
                        SELECT m.phone
                        FROM member m
                        WHERE m.grade = 'VIP'
                        AND NOT EXISTS (
                        SELECT 1 FROM point_history p
                        WHERE p.phone = m.phone
                        AND p.create_at >= NOW() - INTERVAL 1 MONTH)
                        """)
                .rowMapper((rs, rowNum) -> rs.getString("phone"))
                .build();
    }
}
