package com.sping.billage.domain.member.dto;

import com.sping.billage.domain.member.enums.MemberRole;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "회원 정보 응답")
public record MemberResponse(

        @Schema(description = "회원 ID")
        Long id,

        @Schema(description = "이메일")
        String email,

        @Schema(description = "닉네임")
        String nickname,

        @Schema(description = "권한")
        MemberRole role,

        @Schema(description = "보유 포인트")
        long pointBalance,

        @Schema(description = "가입 일시")
        LocalDateTime createdAt
) {
}
