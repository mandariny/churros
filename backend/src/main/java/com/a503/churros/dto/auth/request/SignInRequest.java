package com.a503.churros.dto.auth.request;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Slf4j
@Data
public class SignInRequest {
//    @Schema( type = "string", example = "string@aa.bb", description="계정 이메일 입니다.")
    @NotBlank
    @NotNull
    @Email
    private String email;

//    @Schema( type = "string", example = "string", description="계정 비밀번호 입니다.")
//    @NotBlank
//    @NotNull
    private String password;
}
