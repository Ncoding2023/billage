package com.travel.billage.domain.rental;

public enum RentalStatus {
    REQUESTED("대여신청"),
    RENTING("대여중"),
    RETURN_PENDING("반납대기"),
    RETURN_COMPLETED("반납완료"),
    CANCELED("대여취소");

    private final String description;

    RentalStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
