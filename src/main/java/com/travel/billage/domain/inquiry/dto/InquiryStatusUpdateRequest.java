package com.travel.billage.domain.inquiry.dto;

import com.travel.billage.domain.inquiry.InquiryStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record InquiryStatusUpdateRequest(
        @NotNull InquiryStatus processStatus,
        @NotBlank
        @Size(max = 1000)
        String adminComment
) {
}