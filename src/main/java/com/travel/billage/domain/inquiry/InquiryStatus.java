package com.travel.billage.domain.inquiry;

public enum InquiryStatus {
    RECEIVED("접수"),
    PROCESSING("처리중"),
    COMPLETED("처리완료");

    private final String description;

    InquiryStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
