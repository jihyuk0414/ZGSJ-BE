package com.example.Attendance.config.attendanceJob;

import com.example.Attendance.config.attendanceJob.step.attendance.SalaryBatchState;
import com.example.Attendance.config.attendanceJob.step.email.EmailBatchState;
import com.example.Attendance.config.attendanceJob.step.pdf.PdfBatchState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;


@Slf4j
@Configuration
@RequiredArgsConstructor
public class AttendanceBatchJobListener {

    private final PdfBatchState pdfBatchState;
    private final EmailBatchState emailBatchState;
    private final SalaryBatchState salaryBatchState;
    private final Map<String, Long> stepStartTimes = new ConcurrentHashMap<>();

//    @Bean
//    public JobExecutionListener attendanceJobListener() {
//        return new JobExecutionListener() {
//            @Override
//            public void afterJob(JobExecution jobExecution) {
//                pdfBatchState.reset();
//                emailBatchState.reset();
//                salaryBatchState.reset();
//            }
//        };
//    }

    //타임 check
    @Bean
    public JobExecutionListener attendanceJobListener() {
        return new JobExecutionListener() {
            @Override
            public void beforeJob(JobExecution jobExecution) {
                log.info("==== Job '{}' 시작 ====", jobExecution.getJobInstance().getJobName());
                stepStartTimes.clear(); // 시작 시 초기화
            }

            @Override
            public void afterJob(JobExecution jobExecution) {
                pdfBatchState.reset();
                emailBatchState.reset();
                salaryBatchState.reset();
                long jobExecutionTime = java.time.Duration.between(
                        jobExecution.getStartTime(),
                        jobExecution.getEndTime()
                ).toMillis();
                log.info("==== Job 전체 실행 시간: {} ms ====", jobExecutionTime);

                for (StepExecution stepExecution : jobExecution.getStepExecutions()) {
                    long executionTime = java.time.Duration.between(
                            stepExecution.getStartTime(),
                            stepExecution.getEndTime()
                    ).toMillis();

                    log.info("==== Step '{}' 실행 통계 ====", stepExecution.getStepName());
                    log.info("실행 시간: {} ms", executionTime);
                    log.info("읽은 아이템 수: {}", stepExecution.getReadCount());
                    log.info("처리된 아이템 수: {}", stepExecution.getWriteCount());
                    log.info("건너뛴 아이템 수: {}", stepExecution.getSkipCount());
                    if (Objects.nonNull(stepExecution.getFailureExceptions()) && !stepExecution.getFailureExceptions().isEmpty()) {
                        log.info("실패 원인: {}", stepExecution.getFailureExceptions());
                    }
                    log.info("==========================");
                }

                log.info("==== Job '{}' 종료 - 최종 상태: {} ====",
                        jobExecution.getJobInstance().getJobName(),
                        jobExecution.getStatus());
            }
        };
    }

    @Bean
    public StepExecutionListener stepTimeListener() {
        return new StepExecutionListener() {
            @Override
            public void beforeStep(StepExecution stepExecution) {
                stepStartTimes.put(stepExecution.getStepName(), System.currentTimeMillis());
            }

            @Override
            public ExitStatus afterStep(StepExecution stepExecution) {
                return stepExecution.getExitStatus();
            }
        };
    }


}
