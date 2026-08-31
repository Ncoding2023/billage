package com.travel.billage.domain.inquiry.dto;

import com.travel.billage.domain.inquiry.InquiryStatus;
import jakarta.validation.constraints.NotNull;

public record InquiryStatusUpdateRequest(
        @NotNull InquiryStatus processStatus
) {
}
