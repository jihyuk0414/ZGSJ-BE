package com.example.User.service;

import com.example.User.dto.storeemployee.StoreEmployeeRequest;
import com.example.User.dto.storeemployee.StoreEmployeeUpdateRequest;
import com.example.User.error.CustomException;
import com.example.User.error.ErrorCode;
import com.example.User.model.Store;
import com.example.User.model.StoreEmployee;
import com.example.User.repository.StoreEmployeeRepository;
import com.example.User.repository.StoreRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@RequiredArgsConstructor
@Validated
public class StoreEmployeeService {
    private final StoreRepository storeRepository;
    private final StoreEmployeeRepository storeEmployeeRepository;

    public void register(@Valid StoreEmployeeRequest request, Integer storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new CustomException(ErrorCode.NO_STORE));
        StoreEmployee storeEmployee = request.toEntity(store);
        storeEmployeeRepository.save(storeEmployee);
    }

    @Transactional
    public void updateEmployee(int storeEmployeeId, @Valid StoreEmployeeUpdateRequest request)
    {
        storeEmployeeRepository.updateStoreEmployee(
                storeEmployeeId, request.getName(), request.getSex(),
                request.getAddress(), request.getBirthDate(), request.getPhoneNumber(),
                request.getEmail(), request.getSalary(), request.getEmploymentType(),
                request.getBankCode(), request.getAccountNumber(), request.getPaymentDate()
        );
    }

    @Transactional
    public void deleteEmployee(int storeEmployeeId) {
        StoreEmployee employee = storeEmployeeRepository.findById(storeEmployeeId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        storeEmployeeRepository.delete(employee);
    }
}
