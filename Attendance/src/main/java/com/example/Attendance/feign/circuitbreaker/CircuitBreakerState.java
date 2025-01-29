//package com.example.Attendance.feign.circuitbreaker;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class CircuitBreakerState {
//    private volatile boolean isOpen = false;
//    private volatile long openTime = 0;
//    private final long waitDuration = 10000;
//
//    public boolean isCircuitOpen() {
//        if (isOpen && System.currentTimeMillis() - openTime > waitDuration) {
//            log.info("Circuit breaker 상태가 CLOSED로 변경됨");
//            isOpen = false; // 대기 시간이 지나면 회로 닫기
//            return false;
//        }
//        return isOpen;
//    }
//
//    public void openCircuit() {
//        log.info("Circuit breaker 상태가 OPEN으로 변경됨");
//        isOpen = true;
//        openTime = System.currentTimeMillis();
//    }
//}
