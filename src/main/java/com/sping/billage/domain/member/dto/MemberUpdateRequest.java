package com.sping.billage.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(description = "회원 정보 수정 요청 (변경할 항목만 전달)")
public record MemberUpdateRequest(

        @Schema(description = "닉네임")
        @Size(min = 2, max = 30, message = "닉네임은 2자 이상 30자 이하여야 합니다.")
        String nickname,

        @Schema(description = "비밀번호")
        @Size(min = 8, max = 30, message = "비밀번호는 8자 이상 30자 이하여야 합니다.")
        String password
) {
}
