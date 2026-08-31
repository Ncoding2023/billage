package com.travel.billage.domain.member.dto;

import com.travel.billage.domain.point.PointHistory;
import com.travel.billage.domain.point.PointType;
import java.time.LocalDateTime;

public record PointHistoryResponse(
        Long pointHistoryNo,
        Long rentalNo,
        Integer pointAmount,
        PointType pointType,
        String pointContent,
        LocalDateTime changedAt
) {
    public static PointHistoryResponse from(PointHistory pointHistory) {
        return new PointHistoryResponse(
                pointHistory.getPointHistoryNo(),
                pointHistory.getRental() != null ? pointHistory.getRental().getRentalNo() : null,
                pointHistory.getPointAmount(),
                pointHistory.getPointType(),
                pointHistory.getPointContent(),
                pointHistory.getChangedAt()
        );
    }
}
