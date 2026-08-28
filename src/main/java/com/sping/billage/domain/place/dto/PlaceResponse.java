package com.sping.billage.domain.place.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "물품 보관 장소")
public record PlaceResponse(

        @Schema(description = "주소")
        String address,

        @Schema(description = "상세 주소")
        String detailAddress,

        @Schema(description = "위도 (카카오맵 표시용)")
        BigDecimal latitude,

        @Schema(description = "경도 (카카오맵 표시용)")
        BigDecimal longitude
) {
}
