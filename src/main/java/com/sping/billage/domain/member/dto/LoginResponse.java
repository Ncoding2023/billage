package com.sping.billage.domain.member.dto;

import com.sping.billage.domain.member.enums.MemberRole;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "로그인 응답")
public record LoginResponse(

        @Schema(description = "액세스 토큰")
        String accessToken,

        @Schema(description = "토큰 타입", example = "Bearer")
        String tokenType,

        @Schema(description = "토큰 만료 시간(초)")
        long expiresIn,

        @Schema(description = "회원 ID")
        Long memberId,

        @Schema(description = "닉네임")
        String nickname,

        @Schema(description = "권한")
        MemberRole role
) {
    public static LoginResponse of(String accessToken, long expiresIn,
                                   Long memberId, String nickname, MemberRole role) {
        return new LoginResponse(accessToken, "Bearer", expiresIn, memberId, nickname, role);
    }
}
