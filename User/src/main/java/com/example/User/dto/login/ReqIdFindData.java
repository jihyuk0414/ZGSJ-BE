package com.example.User.dto.login;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class ReqIdFindData {
    @NotBlank(message = "이름은 필수 입력값입니다")
    private String name;

    @NotBlank(message = "전화번호는 필수 입력값입니다")
    @JsonProperty("phone_number")
    private String phoneNumber;
}
