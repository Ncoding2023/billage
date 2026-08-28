package com.sping.billage.domain.item.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "물품 상세 이미지")
public record ItemImageResponse(

        @Schema(description = "이미지 ID")
        Long id,

        @Schema(description = "원본 파일명")
        String originalFileName,

        @Schema(description = "이미지 경로", example = "/upload/8f2c....png")
        String imagePath
) {
}
