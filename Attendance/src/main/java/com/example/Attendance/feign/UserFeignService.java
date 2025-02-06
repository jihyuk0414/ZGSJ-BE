package com.example.Attendance.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Component
//@FeignClient(name = "User",url = "http://localhost:7070/user")
@FeignClient(name = "User",url = "http://user-service.default.svc.cluster.local:7070")
public interface UserFeignService {

    @PostMapping("/employee/masking/list")
    Boolean sendMaskingIds(@RequestBody List<Integer> maskingIds);
}
