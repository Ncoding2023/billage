package com.sping.billage.domain.point.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "포인트 이력")
public record PointHistoryResponse(

        @Schema(description = "이력 ID")
        Long id,

        @Schema(description = "포인트 금액 (적립 +, 사용 -)")
        Long amount,

        @Schema(description = "내용")
        String description,

        @Schema(description = "관련 대여 ID (없을 수 있음)")
        Long rentalId,

        @Schema(description = "발생 일시")
        LocalDateTime createdAt
) {
}
