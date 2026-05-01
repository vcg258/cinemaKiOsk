package com.example.cinemakiosk.batch;


import com.example.cinemakiosk.repository.StatisticsRepository;
import com.example.cinemakiosk.vo.StatisticsVO;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDate;
import java.util.List;

@Log4j2
@Configuration
@RequiredArgsConstructor
@EnableScheduling // 스케줄링 기능 활성화 (@Scheduled 어토네이션 사용 가능)
public class StatisticConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final StatisticsRepository statisticsRepository;

    @Bean
    public Job statisticsJob() {
        return new JobBuilder("statisticsJob", jobRepository)
                .start(generateStep())   // 1. 삽입
                .build();
    }

    @Bean
    public Step generateStep() {
        return new StepBuilder("generateStep", jobRepository)
                .tasklet(generateTasklet(), transactionManager)
                .build();
    }

    @Bean
    @StepScope
    public Tasklet generateTasklet() {
        return new Tasklet() {
            @Override
            @Nullable
            public RepeatStatus execute(StepContribution contribution,
                                        ChunkContext chunkContext) throws Exception {
                String targetDateStr = chunkContext.getStepContext()
                        .getJobParameters()
                        .get("targetDate")
                        .toString();
                LocalDate targetDate = LocalDate.parse(targetDateStr);

                List<StatisticsVO> statisticsVOList = statisticsRepository.selectAggregateByDate(targetDate);
                log.info("StatisticsBatch 집계 결과 {}건", statisticsVOList.size());
                for (StatisticsVO statisticsVO : statisticsVOList) {
                    log.info("statisticsVO: {}", statisticsVO);
                    statisticsRepository.insertStatistics(statisticsVO);
                }
                log.info("StatisticsBatch 통계 저장 완료 (date={})", targetDate);

                return RepeatStatus.FINISHED;
            }
        };
    }
}