package com.travel.billage.domain.item.dto;

import com.travel.billage.domain.category.Category;
import com.travel.billage.domain.image.ItemImage;
import com.travel.billage.domain.item.Item;
import com.travel.billage.domain.item.ItemStatus;
import java.time.LocalDateTime;
import java.util.List;

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
        String mainImagePath,
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
                resolveMainImagePath(item.getImages()),
                item.getCreatedAt(),
                item.getUpdatedAt()
        );
    }

    private static String resolveMainImagePath(List<ItemImage> images) {
        if (images == null || images.isEmpty()) {
            return null;
        }
        return images.stream()
                .filter(ItemImage::isMainImage)
                .map(ItemImage::getImagePath)
                .findFirst()
                .orElse(images.get(0).getImagePath());
    }
}
