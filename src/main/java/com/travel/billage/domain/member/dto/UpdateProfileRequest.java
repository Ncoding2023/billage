package com.travel.billage.domain.member.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateProfileRequest(
        @NotBlank String name,
        @NotBlank String nickname,
        @NotBlank String phone
) {
}
