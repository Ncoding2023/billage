package com.travel.billage.domain.point;

public enum PointType {
    SIGNUP_BONUS("가입적립"),
    RENTAL_PAYMENT("대여차감"),
    RENTAL_INCOME("대여지급"),
    RENTAL_REFUND("대여취소환불");

    private final String description;

    PointType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
