package com.example.Attendance.controller;

import com.example.Attendance.dto.CommuteByPresidentRequest;
import com.example.Attendance.dto.CommuteDailyResponse;
import com.example.Attendance.dto.CommuteMonthlyResponse;
import com.example.Attendance.service.CommuteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/commute")
@Slf4j
public class CommuteController {

    private final CommuteService commuteService;

    @GetMapping("/monthly")
    public ResponseEntity<List<CommuteMonthlyResponse>> getMonthlyCommuteList(
            @RequestParam int storeid,
            @RequestParam int year,
            @RequestParam int month
    ) {
        return ResponseEntity.ok(commuteService.getMonthlyCommuteList(storeid, year, month));
    }

    @GetMapping("/daily")
    public ResponseEntity<List<CommuteDailyResponse>> getDailyCommuteList(
            @RequestParam int storeid,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate commutedate
    ) {
        log.info("조회 요청 날짜 : {}", commutedate);
        log.info("new ver");
        return ResponseEntity.ok(commuteService.getDailyCommuteList(storeid, commutedate));
    }

    @PostMapping
    public ResponseEntity<String> addDailyCommuteByPresident(
            @RequestParam int seid, @Valid @RequestBody CommuteByPresidentRequest request)
    {
        log.info("추가 요청 날짜 : {}", request.getCommuteDate());
        commuteService.addDailyCommuteByPresident(request, seid);
        return ResponseEntity.ok("직원 출 퇴근 사장님이 추가 성공");
    }

    @PutMapping
    public ResponseEntity<String> updateDailyCommuteByPresident(
            @RequestParam int commuteid, @Valid @RequestBody CommuteByPresidentRequest request)
    {
        log.info("변경 요청 날짜 : {}", request.getCommuteDate());
        commuteService.updateDailyCommuteByPresident(request, commuteid);
        return ResponseEntity.ok("직원 출 퇴근 사장님이 수정 성공");
    }
    @DeleteMapping
    public ResponseEntity<String> deleteDailyCommuteByPresident(
            @RequestParam int commuteid)
    {
        commuteService.deleteDailyCommuteByPresident(commuteid);
        return ResponseEntity.ok("출 퇴근 기록 삭제 성공");
    }
}
