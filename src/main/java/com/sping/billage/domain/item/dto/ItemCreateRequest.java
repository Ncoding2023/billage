package com.sping.billage.domain.item.dto;

import com.sping.billage.domain.item.enums.ItemCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Schema(description = "물품 등록 요청 (multipart/form-data)")
public record ItemCreateRequest(

        @Schema(description = "물품명", example = "전동 드릴")
        @NotBlank(message = "물품명은 필수입니다.")
        @Size(max = 200, message = "물품명은 200자 이하여야 합니다.")
        String name,

        @Schema(description = "물품 설명")
        String description,

        @Schema(description = "대여에 필요한 포인트", example = "3000")
        @NotNull(message = "대여 포인트는 필수입니다.")
        @Min(value = 0, message = "대여 포인트는 0 이상이어야 합니다.")
        Long rentalPoint,

        @Schema(description = "카테고리", example = "TOOL")
        @NotNull(message = "카테고리는 필수입니다.")
        ItemCategory category,

        @Schema(description = "주소", example = "서울 강남구 테헤란로 1")
        @Size(max = 100, message = "주소는 100자 이하여야 합니다.")
        String address,

        @Schema(description = "상세 주소", example = "101동 1001호")
        @Size(max = 100, message = "상세 주소는 100자 이하여야 합니다.")
        String detailAddress,

        @Schema(description = "위도", example = "37.5012743")
        BigDecimal latitude,

        @Schema(description = "경도", example = "127.039585")
        BigDecimal longitude
) {
}
