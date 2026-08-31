package com.travel.billage.domain.rental.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record RentalRequest(
        @NotNull Long itemNo,
        @NotNull @FutureOrPresent LocalDate rentalStartDate,
        @NotNull @FutureOrPresent LocalDate rentalEndDate
) {
}
