package com.sping.billage.domain.item.mapper;

import com.sping.billage.domain.item.dto.ItemImageResponse;
import com.sping.billage.domain.item.entity.ItemImage;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ItemImageMapper {

    ItemImageResponse toResponse(ItemImage itemImage);

    List<ItemImageResponse> toResponses(List<ItemImage> itemImages);
}
