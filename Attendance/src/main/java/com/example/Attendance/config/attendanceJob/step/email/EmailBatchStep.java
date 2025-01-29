package com.example.Attendance.config.attendanceJob.step.email;

import com.example.Attendance.feign.UserFeignService;
import com.example.Attendance.service.StoreEmployeeService;
import com.example.Attendance.service.batch.BatchService;
import com.example.Attendance.dto.batch.email.EmailInputData;
import com.example.Attendance.dto.batch.email.EmailOutputData;
import com.example.Attendance.error.CustomException;
import com.example.Attendance.error.ErrorCode;
import com.example.Attendance.service.EmailService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor

public class EmailBatchStep {

    private final EmailBatchState emailBatchState;
    private final BatchService batchService;
    private final EmailService emailService;

    @Bean("emailReader")
    public ItemReader<EmailInputData> emailReader() {
        return () -> {
            try {
                if (emailBatchState.getBatches()==null){
                    emailBatchState.findAllByLocalDate(
                            batchService.findAllByLocalDate(LocalDate.now()));
                }
                return emailBatchState.findBatchInputData();
            } catch (Exception e) {
                throw new CustomException(ErrorCode.API_SERVER_ERROR);
            }
        };
    }

    @Bean("emailProcessor")
    public ItemProcessor<EmailInputData, EmailOutputData> emailProcessor() {
        return item -> {
            try {
                log.info("결과 : {} {} {}",item.getBankResult(),item.getPdfResult(),item.getBatchId());
                if (item.getBankResult()){
                    if (!item.getIsCharge())
                        emailService.sendChargeFail(
                                item.getPresidentEmail(),
                                item.getSalary());

                    if (item.getPdfResult())
                        emailService.sendPayStatement(
                                item.getEmployeeEmail(),
                                item.getUrl());
                    else
                        emailService.sendPdfFail(
                                item.getPresidentEmail(),
                                item.getName(),
                                item.getIssuanceDate()
                                );
                } else{
                    emailService.sendBankFail(
                            item.getPresidentEmail(),
                            item.getName(),
                            item.getIssuanceDate(),
                            item.getMessage());
                }

                return EmailOutputData.of(
                        item.getSeId(),
                        item.getBatchId(),
                        true,
                        item.getIsMask());
            } catch (Exception e) {
                log.error("Email 처리 중 오류: ", e);
                throw new CustomException(ErrorCode.SERVER_ERROR);
            }
        };
    }

    @Bean("emailWriter")
    public ItemWriter<EmailOutputData> emailWriter() {
        return chunk -> {
            List<Integer> batchIds = chunk.getItems().stream()
                    .filter(EmailOutputData::getResult)
                    .map(EmailOutputData::getBatchId)
                    .toList();
            batchService.updateEmailResultByIds(batchIds);
        };
    }
}
