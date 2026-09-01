package com.travel.billage.domain.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SignUpRequest(
        @NotBlank @Email String email,
        @NotBlank
        @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다.")
        @Pattern(
                regexp = ".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?`~].*",
                message = "비밀번호에 특수문자를 포함해야 합니다."
        )
        String password,
        @NotBlank String name,
        @NotBlank String nickname,
        @NotBlank String phone
) {
}
