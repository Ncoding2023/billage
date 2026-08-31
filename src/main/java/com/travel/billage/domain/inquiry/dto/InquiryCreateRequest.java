package com.travel.billage.domain.inquiry.dto;

import com.travel.billage.domain.inquiry.InquiryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record InquiryCreateRequest(
        @NotNull InquiryType inquiryType,
        @NotBlank String inquiryContent
) {
}
