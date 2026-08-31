package com.travel.billage.domain.member.dto;

import com.travel.billage.domain.member.Member;
import com.travel.billage.domain.member.MemberRole;

public record MemberResponse(
        Long memberNo,
        String email,
        String name,
        String nickname,
        String phone,
        MemberRole role,
        boolean enabled
) {
    public static MemberResponse from(Member member) {
        return new MemberResponse(
                member.getMemberNo(),
                member.getEmail(),
                member.getName(),
                member.getNickname(),
                member.getPhone(),
                member.getRole(),
                member.isEnabled()
        );
    }
}
