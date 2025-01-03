package com.example.User.controller;

import com.example.User.dto.authserver.AuthServerEmailPinNumberRequest;
import com.example.User.dto.authserver.AuthServerOnlyPinNumberRequest;
import com.example.User.dto.authserver.AuthServerPinNumberRequest;
import com.example.User.dto.corebank.AccountAndCodeRequest;
import com.example.User.dto.authserver.AuthServerProfileRequest;
import com.example.User.error.CustomException;
import com.example.User.error.ErrorCode;
import com.example.User.dto.response.ResponseDto;
import com.example.User.resolver.MasterId;
import com.example.User.service.CoreBankService;
import com.example.User.service.PresidentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/core/account")
public class AccountAuthController {

    private final CoreBankService coreBankService;
    private final PresidentService presidentService;

    @PostMapping("/check")
    public ResponseEntity<ResponseDto> getAccountBankCodeAndAccountNumber(
            @MasterId Integer id,
            @RequestBody AccountAndCodeRequest accountAndCodeRequest) {
        // 서비스 호출
        boolean isValid = coreBankService.getNameByIdAndBankCodeAndAccountNumber(
                id,
                accountAndCodeRequest.getAccountNumber(),
                accountAndCodeRequest.getBankCode()
        );

        return isValid
                ? ResponseEntity.ok(ResponseDto.from("ok"))
                : ResponseEntity.badRequest().body(ResponseDto.from("일치하지 않는 계좌 정보입니다"));
    }

    @PostMapping("/profile")
    public ResponseEntity<ResponseDto> findUserToAuthServer(@RequestBody AuthServerProfileRequest profileRequest){
        boolean result= coreBankService.verifyProfile(profileRequest);

        return result
                ? ResponseEntity.ok(ResponseDto.from("ok"))
                : ResponseEntity.badRequest().body(ResponseDto.from("존재하지 않는 이메일 정보입니다"));
    }

    @PostMapping("/email/pin")
    public ResponseEntity<ResponseDto> checkAuthEmailPinNumber(@RequestBody AuthServerEmailPinNumberRequest emailPinNumber){
        boolean result= coreBankService.checkEmailPinNumber(emailPinNumber);
        return result
                ? ResponseEntity.ok(ResponseDto.from("ok"))
                : ResponseEntity.badRequest().body(ResponseDto.from("이메일로 전송된 인증번호가 일치하지 않습니다"));
    }

    @PostMapping("/pin")
    public ResponseEntity<ResponseDto> checkAuthPinNumber(@RequestBody AuthServerPinNumberRequest pinNumber){
        boolean result= coreBankService.checkPinNumber(pinNumber);
        return result
                ? ResponseEntity.ok(ResponseDto.from("ok"))
                : ResponseEntity.badRequest().body(ResponseDto.from("금융인증서 대체 PIN 번호가 일치하지 않습니다"));
    }

    @PostMapping("/certificate/pin")
    public ResponseEntity<?> certificateCheckOnlyPinNumber(@MasterId Integer id,
                                                           @RequestBody AuthServerOnlyPinNumberRequest pinNumberRequest ){
        AuthServerPinNumberRequest request= presidentService.
                findByIdToPinNumberRequest(id,pinNumberRequest.getPinNumber());
        boolean result=coreBankService.checkPinNumber(request);
        if (!result)
            throw new CustomException(ErrorCode.INVALID_PIN_NUMBER);
        return ResponseEntity.ok("성공했습니다.");
    }
}
