package com.example.Attendance.config.attendanceJob;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
@Slf4j
public class BatchCheckRunner implements CommandLineRunner {

    private final JobLauncher jobLauncher;
    private final Job attendanceJob;
    private final JobRepository jobRepository;

    @Override
    public void run(String... args) {
        log.info("BatchCheckRunner 시작");
        // 애플리케이션이 완전히 초기화될 때까지 잠시 대기
        try {
            Thread.sleep(10000);  // 10초 대기
            checkAndRunBatch();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("배치 실행 중 인터럽트 발생", e);
        }
    }
    private void checkAndRunBatch() {
        try {
            String dateParam = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            log.info("배치 체크 시작: {}", dateParam);

            log.info("JobParameters 생성 시작");
            JobParameters params = new JobParametersBuilder()
                    .addString("date", dateParam)
                    .toJobParameters();
            log.info("JobParameters 생성 완료: {}", params);

            log.info("마지막 실행 내역 조회 시작");
            JobExecution lastExecution = jobRepository
                    .getLastJobExecution("automaticTransferJob", params);
            log.info("마지막 실행 내역 조회 완료: {}", lastExecution);

            if (lastExecution == null) {
                log.info("오늘 배치 미실행. 배치 실행");
                JobExecution execution = jobLauncher.run(attendanceJob, params);
                log.info("배치 실행 완료. 상태: {}", execution.getStatus());
            } else if(lastExecution.getStatus().isUnsuccessful()) {
                log.info("이전 실패된 기록이 존재합니다. 해당 내역 재 실행합니다");
                String failedStep = lastExecution.getStepExecutions().stream()
                        .filter(stepExecution -> stepExecution.getStatus().isUnsuccessful())
                        .findFirst()
                        .map(stepExecution -> stepExecution.getStepName())
                        .orElse(null);

                JobParameters newParams = new JobParametersBuilder(params)
                        .addString("restart", failedStep)
                        .addLong("time", System.currentTimeMillis())
                        .toJobParameters();

                jobLauncher.run(attendanceJob, newParams);
            }
        } catch (Exception e) {
            log.error("배치 체크 실패", e);  // 스택트레이스 전체 출력
        }
    }
}