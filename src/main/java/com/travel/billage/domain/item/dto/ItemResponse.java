package com.travel.billage.domain.item.dto;

import com.travel.billage.domain.category.Category;
import com.travel.billage.domain.item.Item;
import com.travel.billage.domain.item.ItemStatus;
import java.time.LocalDateTime;

public record ItemResponse(
        Long itemNo,
        Long memberNo,
        String ownerNickname,
        Category category,
        String itemName,
        String description,
        Integer rentalPoint,
        String rentalPlaceName,
        String rentalPlace,
        String rentalPlaceDetail,
        Double latitude,
        Double longitude,
        ItemStatus itemStatus,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ItemResponse from(Item item) {
        return new ItemResponse(
                item.getItemNo(),
                item.getMember().getMemberNo(),
                item.getMember().getNickname(),
                item.getCategory(),
                item.getItemName(),
                item.getDescription(),
                item.getRentalPoint(),
                item.getRentalPlaceName(),
                item.getRentalPlace(),
                item.getRentalPlaceDetail(),
                item.getLatitude(),
                item.getLongitude(),
                item.getItemStatus(),
                item.getCreatedAt(),
                item.getUpdatedAt()
        );
    }
}
