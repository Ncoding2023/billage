package com.sping.billage.domain.item.dto;

import com.sping.billage.domain.item.enums.ItemCategory;
import com.sping.billage.domain.item.enums.ItemStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "물품 목록 항목")
public record ItemListResponse(

        @Schema(description = "물품 ID")
        Long id,

        @Schema(description = "물품명")
        String name,

        @Schema(description = "대여 포인트")
        Long rentalPoint,

        @Schema(description = "상태")
        ItemStatus status,

        @Schema(description = "카테고리")
        ItemCategory category,

        @Schema(description = "카테고리 표시명", example = "공구")
        String categoryName,

        @Schema(description = "대표 이미지 경로")
        String thumbnailPath,

        @Schema(description = "소유자 닉네임")
        String ownerNickname,

        @Schema(description = "주소")
        String address,

        @Schema(description = "등록 일시")
        LocalDateTime createdAt
) {
}
