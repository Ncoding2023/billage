package com.travel.billage.domain.inquiry;

public enum InquiryType {
    REPORT("신고"),
    INQUIRY("문의");

    private final String description;

    InquiryType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
