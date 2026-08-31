package com.travel.billage.domain.member.dto;

public record LoginResponse(
        String accessToken,
        String tokenType,
        MemberResponse member
) {
    public static LoginResponse of(String accessToken, MemberResponse member) {
        return new LoginResponse(accessToken, "Bearer", member);
    }
}
