package com.sping.billage.domain.point.mapper;

import com.sping.billage.domain.point.dto.PointHistoryResponse;
import com.sping.billage.domain.point.entity.PointHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface PointHistoryMapper {

    @Mapping(target = "rentalId", source = "rental.id")
    PointHistoryResponse toResponse(PointHistory pointHistory);
}
