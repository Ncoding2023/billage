package com.sping.billage.domain.item.service;

import com.sping.billage.domain.item.dto.ItemCreateRequest;
import com.sping.billage.domain.item.dto.ItemDetailResponse;
import com.sping.billage.domain.item.dto.ItemListResponse;
import com.sping.billage.domain.item.dto.ItemUpdateRequest;
import com.sping.billage.domain.item.entity.Item;
import com.sping.billage.domain.item.entity.ItemImage;
import com.sping.billage.domain.item.enums.ItemCategory;
import com.sping.billage.domain.item.enums.ItemStatus;
import com.sping.billage.domain.item.mapper.ItemMapper;
import com.sping.billage.domain.item.repository.ItemRepository;
import com.sping.billage.domain.member.entity.Member;
import com.sping.billage.domain.member.repository.MemberRepository;
import com.sping.billage.domain.place.entity.Place;
import com.sping.billage.domain.rental.enums.RentalStatus;
import com.sping.billage.domain.rental.repository.RentalRepository;
import com.sping.billage.global.common.PageResponse;
import com.sping.billage.global.exception.BusinessException;
import com.sping.billage.global.exception.ErrorCode;
import com.sping.billage.global.file.FileStorageService;
import com.sping.billage.global.file.StoredFile;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemService {

    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final RentalRepository rentalRepository;
    private final ItemMapper itemMapper;
    private final FileStorageService fileStorageService;

    @Transactional
    public ItemDetailResponse createItem(Long memberId,
                                         ItemCreateRequest request,
                                         MultipartFile thumbnail,
                                         List<MultipartFile> images) {
        Member owner = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        StoredFile thumbnailFile = null;
        List<StoredFile> detailFiles = List.of();
        try {
            thumbnailFile = storeIfPresent(thumbnail);
            detailFiles = fileStorageService.storeAll(images);

            Item item = itemMapper.toEntity(request, owner,
                    thumbnailFile == null ? null : thumbnailFile.imagePath());

            item.assignPlace(Place.builder()
                    .address(request.address())
                    .detailAddress(request.detailAddress())
                    .latitude(request.latitude())
                    .longitude(request.longitude())
                    .build());

            detailFiles.forEach(file -> item.addImage(toItemImage(file)));

            itemRepository.saveAndFlush(item);
            return itemMapper.toDetailResponse(item);
        } catch (RuntimeException e) {
            deleteQuietly(thumbnailFile, detailFiles);
            throw e;
        }
    }

    public PageResponse<ItemListResponse> getItems(ItemCategory category,
                                                  ItemStatus status,
                                                  String keyword,
                                                  Pageable pageable) {
        String normalizedKeyword = StringUtils.hasText(keyword) ? keyword.trim() : null;
        return PageResponse.of(
                itemRepository.search(category, status, normalizedKeyword, pageable),
                itemMapper::toListResponse);
    }

    public ItemDetailResponse getItem(Long itemId) {
        return itemMapper.toDetailResponse(getItemOrThrow(itemId));
    }

    @Transactional
    public ItemDetailResponse updateItem(Long memberId,
                                         Long itemId,
                                         ItemUpdateRequest request,
                                         MultipartFile thumbnail,
                                         List<MultipartFile> images) {
        Item item = getItemOrThrow(itemId);
        validateOwner(item, memberId);

        StoredFile newThumbnail = null;
        List<StoredFile> newImages = List.of();
        try {
            newThumbnail = storeIfPresent(thumbnail);
            newImages = fileStorageService.storeAll(images);

            item.update(request.name(), request.description(), request.rentalPoint(), request.category());
            updateStatus(item, request.status());
            updatePlace(item, request);

            List<String> replacedPaths = new ArrayList<>();
            if (newThumbnail != null) {
                replacedPaths.add(item.getThumbnailPath());
                item.changeThumbnailPath(newThumbnail.imagePath());
            }
            if (!newImages.isEmpty()) {
                item.getImages().forEach(image -> replacedPaths.add(image.getImagePath()));
                item.clearImages();
                newImages.forEach(file -> item.addImage(toItemImage(file)));
            }

            itemRepository.flush();
            replacedPaths.forEach(fileStorageService::delete);

            return itemMapper.toDetailResponse(item);
        } catch (RuntimeException e) {
            deleteQuietly(newThumbnail, newImages);
            throw e;
        }
    }

    @Transactional
    public void deleteItem(Long memberId, Long itemId) {
        Item item = getItemOrThrow(itemId);
        validateOwner(item, memberId);
        validateDeletable(item);

        List<String> imagePaths = new ArrayList<>();
        imagePaths.add(item.getThumbnailPath());
        item.getImages().forEach(image -> imagePaths.add(image.getImagePath()));

        itemRepository.delete(item);
        itemRepository.flush();
        imagePaths.forEach(fileStorageService::delete);
    }

    /**
     * 다른 도메인(대여 등)에서 사용할 엔티티 조회.
     */
    public Item getItemOrThrow(Long itemId) {
        return itemRepository.findWithOwnerAndPlaceById(itemId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ITEM_NOT_FOUND));
    }

    private void validateOwner(Item item, Long memberId) {
        if (!item.isOwnedBy(memberId)) {
            throw new BusinessException(ErrorCode.NOT_ITEM_OWNER);
        }
    }

    private void validateDeletable(Item item) {
        if (item.getStatus() == ItemStatus.RENTED
                || rentalRepository.existsByItemIdAndStatusIn(item.getId(), RentalStatus.activeStatuses())) {
            throw new BusinessException(ErrorCode.ITEM_DELETE_NOT_ALLOWED);
        }
        if (rentalRepository.existsByItemId(item.getId())) {
            throw new BusinessException(ErrorCode.ITEM_DELETE_NOT_ALLOWED,
                    "대여 이력이 있는 물품은 삭제할 수 없습니다. 대여 불가(UNAVAILABLE) 상태로 변경해 주세요.");
        }
    }

    private void updateStatus(Item item, ItemStatus status) {
        if (status == null || status == item.getStatus()) {
            return;
        }
        if (status == ItemStatus.RENTED) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE,
                    "대여 상태는 대여 승인/반납 처리로만 변경됩니다.");
        }
        if (item.getStatus() == ItemStatus.RENTED) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE,
                    "대여 중인 물품의 상태는 변경할 수 없습니다.");
        }
        item.changeStatus(status);
    }

    private void updatePlace(Item item, ItemUpdateRequest request) {
        if (item.getPlace() == null) {
            item.assignPlace(Place.builder()
                    .address(request.address())
                    .detailAddress(request.detailAddress())
                    .latitude(request.latitude())
                    .longitude(request.longitude())
                    .build());
            return;
        }
        item.getPlace().update(request.address(), request.detailAddress(),
                request.latitude(), request.longitude());
    }

    /**
     * 저장은 성공했지만 이후 처리가 실패한 경우 디스크에 남는 파일을 정리한다.
     */
    private void deleteQuietly(StoredFile thumbnail, List<StoredFile> images) {
        if (thumbnail != null) {
            fileStorageService.delete(thumbnail.imagePath());
        }
        images.forEach(file -> fileStorageService.delete(file.imagePath()));
    }

    private StoredFile storeIfPresent(MultipartFile file) {
        return (file == null || file.isEmpty()) ? null : fileStorageService.store(file);
    }

    private ItemImage toItemImage(StoredFile file) {
        return ItemImage.builder()
                .originalFileName(file.originalFileName())
                .storedFileName(file.storedFileName())
                .imagePath(file.imagePath())
                .build();
    }
}
