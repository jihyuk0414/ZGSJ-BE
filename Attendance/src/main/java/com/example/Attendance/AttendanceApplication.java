package com.example.Attendance;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

import java.sql.Time;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.TimeZone;

@SpringBootApplication
@EnableFeignClients
@Slf4j
public class AttendanceApplication {

	public static void main(String[] args) {
		log.info("new ver");
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
		log.info("=== Time Zone Configuration ===");
		log.info("System Default TimeZone: {}", TimeZone.getDefault().getID());
		log.info("Current Server Time: {}", LocalDateTime.now());
		log.info("Default ZoneId: {}", ZoneId.systemDefault());
		log.info("=============================");
		SpringApplication.run(AttendanceApplication.class, args);
	}

}
