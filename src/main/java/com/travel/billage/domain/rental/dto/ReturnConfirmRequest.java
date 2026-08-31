package com.travel.billage.domain.rental.dto;

import com.travel.billage.domain.rental.ReturnStatus;
import jakarta.validation.constraints.NotNull;

public record ReturnConfirmRequest(
        @NotNull ReturnStatus returnStatus,
        String returnMemo
) {
}
