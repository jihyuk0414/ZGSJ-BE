package com.example.Attendance.service;

import com.example.Attendance.dto.EmployeeCommuteRequest;
import com.example.Attendance.dto.batch.BatchInputData;
import com.example.Attendance.error.CustomException;
import com.example.Attendance.error.ErrorCode;
import com.example.Attendance.model.StoreEmployee;
import com.example.Attendance.repository.StoreEmployeeRepository;
import com.example.Attendance.util.Cryptography;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Service
public class StoreEmployeeService {
    private final StoreEmployeeRepository storeEmployeeRepository;
    private final Cryptography cryptography;

    public StoreEmployee findStoreEmployeeByEmailAndStoreId(Integer storeId,EmployeeCommuteRequest request,String encryptedEmail) {

        //이메일 디코딩
        String email  =cryptography.decrypt(encryptedEmail);
        log.info("email여기있음 " + email);
        StoreEmployee storeEmployee=storeEmployeeRepository.findByEmailAndStoreId(email,storeId)
                .orElseThrow(()->new CustomException(ErrorCode.INVALID_EMPLOYEE));
        log.info("email로 직원 찾았음"+storeEmployee);
        Double sLat=storeEmployee.getStore().getLatitude();
        Double sLong=storeEmployee.getStore().getLongitude();
        double rangeLat = 0.0009; // 위도 100m 범위
        double rangeLon = 0.0011; // 경도 100m 범위

//        Double reLat= request.getLatitude();
//        Double reLong= request.getLongitude();
//        boolean isLat=reLat <= sLat+rangeLat && reLat>=sLat-rangeLat;
//        boolean isLong= reLong<= sLong+rangeLon && reLong>=sLong-rangeLon;
//        if (!isLat || !isLong){
//            throw new CustomException(ErrorCode.INVALID_LOCATION);
//        }
        return storeEmployee;
    }

    public List<BatchInputData> findStoreEmployeeByTypeAndPaymentDate(int paymentDay){

        Set<BatchInputData> set =storeEmployeeRepository.findAllByEmploymentType();

        set.addAll(storeEmployeeRepository
//                            .findAllBatchInputDataByPaymentDate(20)); //테스트용
                .findAllBatchInputDataByPaymentDate(paymentDay));

        return set.stream().toList();
    }

    @Transactional
    public void updateEmployeeType(List<Integer> ids){
        storeEmployeeRepository.updateStoreEmployeesByIds(ids);
    }
}
