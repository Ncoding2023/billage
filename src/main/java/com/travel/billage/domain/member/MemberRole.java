package com.travel.billage.domain.member;

public enum MemberRole {
    USER("일반회원"),
    ADMIN("관리자");

    private final String description;

    MemberRole(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
