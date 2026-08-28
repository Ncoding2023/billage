package com.sping.billage.domain.point.dto;

import com.sping.billage.global.common.PageResponse;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "포인트 잔액 및 이력 응답")
public record PointSummaryResponse(

        @Schema(description = "현재 포인트 잔액 (이력 합계)")
        long balance,

        @Schema(description = "포인트 이력")
        PageResponse<PointHistoryResponse> histories
) {
}
