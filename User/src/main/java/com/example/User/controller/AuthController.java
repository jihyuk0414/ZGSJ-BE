package com.example.User.controller;

import com.example.User.dto.login.ReqLoginData;
import com.example.User.dto.login.ReqRegist;
import com.example.User.dto.login.ResNewAccessToken;
import com.example.User.dto.passwordemail.PassWordValidate;
import com.example.User.resolver.MasterId;
import com.example.User.service.AuthService;
import com.example.User.service.PresidentService;
import com.example.User.service.RedisTokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/president")
@Slf4j
@RequiredArgsConstructor
public class AuthController {
    private final PresidentService presidentService;
    private final RedisTokenService redisTokenService;
    private final AuthService authService;

    @PostMapping("/login")
    ResponseEntity<ResNewAccessToken> login(@RequestBody ReqLoginData reqLoginData, HttpServletRequest request) {
        log.info("reqLoginData: "+reqLoginData);

        Integer id= presidentService.validateLogin(reqLoginData);
        String[] tokens = authService.onAuthenticationSuccess(id);


//        ResponseCookie cookie1 = ResponseCookie.from("access_token", tokens[0])
//                .path("/")
//                .httpOnly(true)
//                .maxAge(600)
//                .sameSite("Lax")
//                .build();
//
//        ResponseCookie cookie2 = ResponseCookie.from("refresh_token", tokens[1])
//                .path("/")
//                .httpOnly(true)
//                .maxAge(36000)
//                .sameSite("Lax")
//                .build();
//
//        return ResponseEntity
//                .ok()
//                .header(HttpHeaders.SET_COOKIE, cookie1.toString(),cookie2.toString())
//                .body(ResNewAccessToken.from(tokens[0]));
        return ResponseEntity.ok(ResNewAccessToken.from(tokens[0]));
    }

    @GetMapping("/logout")
    ResponseEntity<Void> logout(@MasterId Integer id) {
        redisTokenService.removeRefreshToken(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/refresh")
    ResponseEntity<ResNewAccessToken> refresh(@MasterId Integer id) {
        String accessToken =redisTokenService.checkRefreshToken(id);

        return ResponseEntity.ok(ResNewAccessToken.from(accessToken));
    }

    @PostMapping("/regist")
    ResponseEntity<ResNewAccessToken> regist(@RequestBody ReqRegist reqRegist) {
        Integer id = presidentService.regist(reqRegist);
//        String accessToken = authService.onAuthenticationSuccess(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/validate-password")
    ResponseEntity<Boolean> validatePassword(
            @MasterId Integer id,
            @Valid @RequestBody PassWordValidate passWordValidate)
    {
        return authService.validatePassword(id, passWordValidate.getPassword())
                ? ResponseEntity.ok().build()
                : ResponseEntity.badRequest().build();
    }
}
