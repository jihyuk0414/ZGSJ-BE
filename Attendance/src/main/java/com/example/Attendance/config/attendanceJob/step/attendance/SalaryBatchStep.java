package com.example.Attendance.config.attendanceJob.step.attendance;

import com.example.Attendance.dto.batch.*;
import com.example.Attendance.error.CustomException;
import com.example.Attendance.error.ErrorCode;
import com.example.Attendance.error.ErrorDTO;
import com.example.Attendance.error.FeignExceptionHandler;
import com.example.Attendance.error.log.ErrorType;
import com.example.Attendance.feign.CoreBankFeignClient;
import com.example.Attendance.feign.FeignWithCoreBank;
import com.example.Attendance.model.Batch;
import com.example.Attendance.repository.BatchRepository;
import com.example.Attendance.service.CommuteService;
import com.example.Attendance.service.StoreEmployeeService;
import com.example.Attendance.service.batch.BatchService;
import com.example.Attendance.service.batch.CalculateService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor

public class SalaryBatchStep {

    private final SalaryBatchState salaryBatchState;
    private final StoreEmployeeService storeEmployeeService;
    private final CommuteService commuteService;
    private final BatchService batchService;
    private final FeignWithCoreBank feignWithCoreBank;
    private final CalculateService calculateService;
    private final FeignExceptionHandler handler;
    private final CoreBankFeignClient coreBankFeignClient;

    @Bean("salaryReader")
    public ItemReader<BatchInputData> salaryReader() {
        return () -> {
            try {
                if (salaryBatchState.getEmployees() == null) {
                    log.info("찾아오기 시도");
                    if (!salaryBatchState.setEmployees(storeEmployeeService.
                            findStoreEmployeeByTypeAndPaymentDate(salaryBatchState.getPaymentDay()))) {
                        log.info("사람없음");
                        return null;
                    }
                    log.info("찾아오기 성공");
                    salaryBatchState.setCommutes(commuteService.
                            findAllByCommuteDateBetween(salaryBatchState.getEmployeeIds(),
                                    salaryBatchState.getLocalDate()));
                }
                return salaryBatchState.findBatchInputData();
            } catch (Exception e) {
                log.error("데이터 읽기 실패: {}", e.getMessage(), e);
                throw new CustomException(ErrorCode.API_SERVER_ERROR);
            }
        };
    }

    @Bean("salaryProcessor")
    public ItemProcessor<BatchInputData, BatchOutputData> salaryProcessor() {
        return item -> {
            try {
                CommuteSummary commuteSummary = salaryBatchState.getCommuteDuration(item.getSeId());
                //여기 들어오기전에 0인애들 필터링 하면 좋을 듯
                if (commuteSummary == null) {
                    return null;
                }
                calculateService.calculate(item, commuteSummary);

                TransferRequest request = TransferRequest.from(item);
                TransferRequest adminRequest = TransferRequest.fromForAdmin(request);

//                TransferResponse response = feignWithCoreBank.automaticTransfer(request);
                //수수료 입금 로직 제거

                TransferResponse response = null;
                try {
                    //프록시 활용, 서킷 브레이커
                    response = coreBankFeignClient.automaticTransfer(request);
                    return BatchOutputData.of(response, item, true);
                } catch (FeignException fe) {
                    // 수수료 이체 실패 시 로깅 및 알림
                    log.error("수수료 이체 실패 - amount={}, error={}", adminRequest.getAmount(), fe.getMessage());

                    // 직원급여는 정상 처리된 것으로 처리
                    return BatchOutputData.of(response, item, false);
                }
            } catch (FeignException fe) {
                log.error("금융서버 통신 실패 - president_account={}, employee_account={}, error={}, type={}",
                        item.getFromAccount(), item.getToAccount(), fe.getMessage(), ErrorType.FEIGN_EXCEPTION.name());
                ErrorDTO dto =  handler.feToErrorDTO(fe);
                return BatchOutputData.ofFail(item,dto.getCode());

            } catch (Exception e) {
                log.error("자동이체 처리 실패 - president_account={}, employee_account={}, error={}, type={}",
                        item.getFromAccount(), item.getToAccount(), e.getMessage(), ErrorType.INTERNAL_ERROR.name());
                return BatchOutputData.ofFail(item,"서버 오류");
            }
        };
    }


    @Bean("salaryWriter")
    public ItemWriter<BatchOutputData> salaryWriter() {
        return chunk -> {
            List<Batch> batches= chunk.getItems().stream()
                    .map(BatchOutputData::ToBatchEntity)
                    .toList();
            batchService.saveAll(batches);

            List<Integer> ids= chunk.getItems().stream()
                    .filter(BatchOutputData::getIsMask)
                    .map(BatchOutputData::getSeId).toList();
            storeEmployeeService.updateEmployeeType(ids);
            log.info("급여 이체 결과 {} 건 저장 완료", chunk.size());
        };
    }
}
