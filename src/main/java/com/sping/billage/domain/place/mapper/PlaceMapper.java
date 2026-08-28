package com.sping.billage.domain.place.mapper;

import com.sping.billage.domain.place.dto.PlaceResponse;
import com.sping.billage.domain.place.entity.Place;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface PlaceMapper {

    PlaceResponse toResponse(Place place);
}
