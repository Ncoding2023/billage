package com.travel.billage.domain.item.dto;

import com.travel.billage.domain.category.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ItemUpdateRequest(
        @NotNull Category category,
        @NotBlank String itemName,
        String description,
        @NotNull @Positive Integer rentalPoint,
        @NotBlank String rentalPlaceName,
        @NotBlank String rentalPlace,
        String rentalPlaceDetail,
        @NotNull Double latitude,
        @NotNull Double longitude
) {
}
