package com.example.Attendance.feign;


import com.example.Attendance.dto.batch.TransferRequest;
import com.example.Attendance.dto.batch.TransferResponse;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;  // 이걸로 변경
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CoreBankFeignClient {
    private final FeignWithCoreBank feignWithCoreBank;
    private final CircuitBreaker circuitBreaker;

    public TransferResponse automaticTransfer(TransferRequest request) {
        try {
            return circuitBreaker.decorateSupplier(() ->
                    feignWithCoreBank.automaticTransfer(request)
            ).get();
        } catch (Exception e) {
            log.error("CoreBank 요청 실패 - 6초간 Circuit Open");
            throw e;
        }
    }
}
