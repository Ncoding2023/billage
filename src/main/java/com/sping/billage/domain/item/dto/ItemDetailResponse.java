package com.sping.billage.domain.item.dto;

import com.sping.billage.domain.item.enums.ItemCategory;
import com.sping.billage.domain.item.enums.ItemStatus;
import com.sping.billage.domain.place.dto.PlaceResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "물품 상세")
public record ItemDetailResponse(

        @Schema(description = "물품 ID")
        Long id,

        @Schema(description = "물품명")
        String name,

        @Schema(description = "물품 설명")
        String description,

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

        @Schema(description = "소유자 ID")
        Long ownerId,

        @Schema(description = "소유자 닉네임")
        String ownerNickname,

        @Schema(description = "보관 장소")
        PlaceResponse place,

        @Schema(description = "상세 이미지 목록")
        List<ItemImageResponse> images,

        @Schema(description = "등록 일시")
        LocalDateTime createdAt,

        @Schema(description = "수정 일시")
        LocalDateTime updatedAt
) {
}
