package com.travel.billage.domain.rental;

public enum ReturnStatus {
    NORMAL("정상"),
    DAMAGED("파손"),
    LOST("분실");

    private final String description;

    ReturnStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
