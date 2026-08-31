package com.travel.billage.domain.image;

import com.travel.billage.common.file.FileStorageService;
import com.travel.billage.domain.item.Item;
import com.travel.billage.domain.item.ItemRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemImageService {

    private final ItemImageRepository itemImageRepository;
    private final ItemRepository itemRepository;
    private final FileStorageService fileStorageService;

    @Transactional
    public ItemImage addImage(Long itemNo, Long memberNo, MultipartFile file, boolean mainImage) {
        Item item = getOwnedItem(itemNo, memberNo);

        if (mainImage) {
            unsetCurrentMainImage(item);
        }

        String storedFileName = fileStorageService.store(file);

        return itemImageRepository.save(ItemImage.builder()
                .item(item)
                .originalFileName(file.getOriginalFilename())
                .storedFileName(storedFileName)
                .imagePath("/uploads/" + storedFileName)
                .mainImage(mainImage)
                .build());
    }

    @Transactional
    public void changeMainImage(Long itemNo, Long imageNo, Long memberNo) {
        Item item = getOwnedItem(itemNo, memberNo);
        unsetCurrentMainImage(item);

        ItemImage newMain = itemImageRepository.findById(imageNo)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이미지입니다."));
        newMain.changeMainImage(true);
    }

    @Transactional
    public void deleteImage(Long imageNo, Long memberNo) {
        ItemImage image = itemImageRepository.findById(imageNo)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이미지입니다."));
        validateOwner(image.getItem(), memberNo);
        itemImageRepository.delete(image);
    }

    public List<ItemImage> getImages(Long itemNo) {
        return itemImageRepository.findByItem(itemRepository.findById(itemNo)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 물품입니다.")));
    }

    private Item getOwnedItem(Long itemNo, Long memberNo) {
        Item item = itemRepository.findById(itemNo)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 물품입니다."));
        validateOwner(item, memberNo);
        return item;
    }

    private void unsetCurrentMainImage(Item item) {
        itemImageRepository.findByItemAndMainImageTrue(item)
                .ifPresent(existing -> existing.changeMainImage(false));
    }

    private void validateOwner(Item item, Long memberNo) {
        if (!item.getMember().getMemberNo().equals(memberNo)) {
            throw new IllegalStateException("물품에 대한 권한이 없습니다.");
        }
    }
}
