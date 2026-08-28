package com.sping.billage.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "로그인 요청")
public record LoginRequest(

        @Schema(description = "이메일", example = "user@billage.com")
        @NotBlank(message = "이메일은 필수입니다.")
        String email,

        @Schema(description = "비밀번호", example = "billage1234")
        @NotBlank(message = "비밀번호는 필수입니다.")
        String password
) {
}
