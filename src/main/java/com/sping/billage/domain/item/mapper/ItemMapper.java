package com.sping.billage.domain.item.mapper;

import com.sping.billage.domain.item.dto.ItemCreateRequest;
import com.sping.billage.domain.item.dto.ItemDetailResponse;
import com.sping.billage.domain.item.dto.ItemListResponse;
import com.sping.billage.domain.item.entity.Item;
import com.sping.billage.domain.member.entity.Member;
import com.sping.billage.domain.place.mapper.PlaceMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        uses = {PlaceMapper.class, ItemImageMapper.class})
public interface ItemMapper {

    @Mapping(target = "name", source = "request.name")
    @Mapping(target = "description", source = "request.description")
    @Mapping(target = "rentalPoint", source = "request.rentalPoint")
    @Mapping(target = "category", source = "request.category")
    @Mapping(target = "thumbnailPath", source = "thumbnailPath")
    @Mapping(target = "owner", source = "owner")
    Item toEntity(ItemCreateRequest request, Member owner, String thumbnailPath);

    @Mapping(target = "categoryName", source = "category.displayName")
    @Mapping(target = "ownerNickname", source = "owner.nickname")
    @Mapping(target = "address", source = "place.address")
    ItemListResponse toListResponse(Item item);

    @Mapping(target = "categoryName", source = "category.displayName")
    @Mapping(target = "ownerId", source = "owner.id")
    @Mapping(target = "ownerNickname", source = "owner.nickname")
    ItemDetailResponse toDetailResponse(Item item);
}
