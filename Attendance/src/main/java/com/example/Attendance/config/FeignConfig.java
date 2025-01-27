package com.example.Attendance.config;

import feign.Retryer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {
    @Bean
    public Retryer retryer() {
        // period: 초기 재시도 간격(ms)
        // maxPeriod: 최대 재시도 간격(ms)
        // maxAttempts: 최대 재시도 횟수
        return new Retryer.Default(10000, 10000, 3);
    }
}