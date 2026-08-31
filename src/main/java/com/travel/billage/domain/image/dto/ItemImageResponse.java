package com.travel.billage.domain.image.dto;

import com.travel.billage.domain.image.ItemImage;
import java.time.LocalDateTime;

public record ItemImageResponse(
        Long imageNo,
        Long itemNo,
        String originalFileName,
        String storedFileName,
        String imagePath,
        boolean mainImage,
        LocalDateTime createdAt
) {
    public static ItemImageResponse from(ItemImage itemImage) {
        return new ItemImageResponse(
                itemImage.getImageNo(),
                itemImage.getItem().getItemNo(),
                itemImage.getOriginalFileName(),
                itemImage.getStoredFileName(),
                itemImage.getImagePath(),
                itemImage.isMainImage(),
                itemImage.getCreatedAt()
        );
    }
}
