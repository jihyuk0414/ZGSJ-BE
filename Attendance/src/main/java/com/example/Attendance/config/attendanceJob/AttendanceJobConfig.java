package com.example.Attendance.config.attendanceJob;

import com.example.Attendance.config.attendanceJob.step.attendance.SalaryBatchStep;
import com.example.Attendance.config.attendanceJob.step.email.EmailBatchStep;
import com.example.Attendance.config.attendanceJob.step.pdf.PdfBatchStep;
import com.example.Attendance.dto.batch.BatchInputData;
import com.example.Attendance.dto.batch.BatchOutputData;
import com.example.Attendance.dto.batch.email.EmailInputData;
import com.example.Attendance.dto.batch.email.EmailOutputData;
import com.example.Attendance.dto.batch.pdf.PdfInputData;
import com.example.Attendance.dto.batch.pdf.PdfOutputData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class AttendanceJobConfig {

    private final SalaryBatchStep salaryBatchStep;
    private final EmailBatchStep emailBatchStep;
    private final PdfBatchStep pdfBatchStep;

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final AttendanceBatchJobListener attendanceBatchJobListener;


    @Bean
    public Job attendanceJob() {
        return new JobBuilder("automaticTransferJob", jobRepository)
                .listener(attendanceBatchJobListener.attendanceJobListener())
                .start(attendanceStep())
                .on("FAILED").fail()  // attendanceStep이 실패하면 Job 전체 실패
                .from(attendanceStep()).on("COMPLETED").to(statementPdfStep())  // 성공시에만 다음 단계
                .next(statementEmailStep())
                .end()
                .build();
    }

    @Bean
    public Step attendanceStep() {
        return new StepBuilder("automaticTransferStep", jobRepository)
                .<BatchInputData, BatchOutputData>chunk(3, transactionManager)
                .reader(salaryBatchStep.salaryReader())       // 데이터 읽기
                .processor(salaryBatchStep.salaryProcessor()) // 데이터 처리
                .writer(salaryBatchStep.salaryWriter())       // 데이터 쓰기
                .build();
    }

    @Bean
    public Step statementPdfStep() {
        return new StepBuilder("statementPdfStep", jobRepository)
                .<PdfInputData, PdfOutputData>chunk(3, transactionManager)
                .reader(pdfBatchStep.pdfReader())       // 데이터 읽기
                .processor(pdfBatchStep.pdfProcessor()) // 데이터 처리
                .writer(pdfBatchStep.pdfWriter())       // 데이터 쓰기
                .build();
    }

    @Bean
    public Step statementEmailStep() {
        return new StepBuilder("statementEmailStep", jobRepository)
                .<EmailInputData, EmailOutputData>chunk(3, transactionManager)
                .reader(emailBatchStep.emailReader())       // 데이터 읽기
                .processor(emailBatchStep.emailProcessor()) // 데이터 처리
                .writer(emailBatchStep.emailWriter())       // 데이터 쓰기
                .build();
    }
}
