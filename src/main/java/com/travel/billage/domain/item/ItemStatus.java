package com.travel.billage.domain.item;

public enum ItemStatus {
    AVAILABLE("대여가능"),
    UNAVAILABLE("대여불가");

    private final String description;

    ItemStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
