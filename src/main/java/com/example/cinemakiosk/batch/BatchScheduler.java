package com.example.cinemakiosk.batch;

import lombok.extern.log4j.Log4j2;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Log4j2
@Component
public class BatchScheduler {

    private final JobLauncher jobLauncher;
    private final Job statisticsJob;
    private final Job memberCleanupJob;

    // Job빈이 여러개이니 @Qualifier를 사용하여 특정 Job을 지정
    public BatchScheduler(JobLauncher jobLauncher,
                          @Qualifier("statisticsJob") Job statisticsJob,
                          @Qualifier("memberCleanupJob") Job memberCleanupJob) {
        this.jobLauncher = jobLauncher;
        this.statisticsJob = statisticsJob;
        this.memberCleanupJob = memberCleanupJob;
    }



    @Scheduled(cron = "0 0 0 * * *")
    public void statisticsRun() {

        // 어제를 가져옴
        LocalDate yesterday = LocalDate.now().minusDays(1);

        log.info("StatisticsBatch 실행 요청 (targetDate={})", yesterday);
        try {
            // 잡파라미터로 어제를 전달
            JobParameters params = new JobParametersBuilder()
                    .addString("targetDate", yesterday.toString())
                    .addLong("runId", System.currentTimeMillis()) // 재실행을 위한 현재시간(더미)를 입력
                    .toJobParameters();
            jobLauncher.run(statisticsJob, params);
        } catch (Exception e) {
            log.error("StatisticsBatch 실행 실패: {}", e.getMessage(), e);
        }
    }

    // 수동실행을 위한 메서드. 테스트코드에서 사용
    public void runJob(LocalDate date) {

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("targetDate", date.toString())
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();
        try {
            jobLauncher.run(statisticsJob, jobParameters);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void runMemberCleanupJob() throws Exception {
        log.info("MemberCleanup 실행 요청");
        try {
            JobParameters params = new JobParametersBuilder()
                    .addLong("runId", System.currentTimeMillis())
                    .toJobParameters();

            jobLauncher.run(memberCleanupJob, params);
        } catch (Exception e) {
            log.error("MemberCleanup 실행 실패: {}", e.getMessage(), e);
        }
    }




    // 수동실행을 위한 메서드. 테스트코드에서 사용
    public void runJob2() {

        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();
        try {
            jobLauncher.run(memberCleanupJob, jobParameters);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

