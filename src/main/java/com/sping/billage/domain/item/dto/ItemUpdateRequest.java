package com.sping.billage.domain.item.dto;

import com.sping.billage.domain.item.enums.ItemCategory;
import com.sping.billage.domain.item.enums.ItemStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Schema(description = "물품 수정 요청 (변경할 항목만 전달, multipart/form-data)")
public record ItemUpdateRequest(

        @Schema(description = "물품명")
        @Size(max = 200, message = "물품명은 200자 이하여야 합니다.")
        String name,

        @Schema(description = "물품 설명")
        String description,

        @Schema(description = "대여에 필요한 포인트")
        @Min(value = 0, message = "대여 포인트는 0 이상이어야 합니다.")
        Long rentalPoint,

        @Schema(description = "카테고리")
        ItemCategory category,

        @Schema(description = "대여 가능 여부 변경 (AVAILABLE / UNAVAILABLE 만 지정 가능)")
        ItemStatus status,

        @Schema(description = "주소")
        @Size(max = 100, message = "주소는 100자 이하여야 합니다.")
        String address,

        @Schema(description = "상세 주소")
        @Size(max = 100, message = "상세 주소는 100자 이하여야 합니다.")
        String detailAddress,

        @Schema(description = "위도")
        BigDecimal latitude,

        @Schema(description = "경도")
        BigDecimal longitude
) {
}
