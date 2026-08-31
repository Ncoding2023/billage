package com.travel.billage.domain.image;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.travel.billage.common.file.FileStorageService;
import com.travel.billage.domain.category.Category;
import com.travel.billage.domain.item.Item;
import com.travel.billage.domain.item.ItemRepository;
import com.travel.billage.domain.member.Member;
import com.travel.billage.domain.member.MemberRole;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class ItemImageServiceTest {

    @Mock
    private ItemImageRepository itemImageRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private ItemImageService itemImageService;

    @Test
    void addImage_asOwner_notMain_savesWithoutTouchingExisting() {
        Item item = createItem(1L);
        MultipartFile file = createFile();
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));
        when(fileStorageService.store(file)).thenReturn("stored.png");
        when(itemImageRepository.save(any(ItemImage.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ItemImage saved = itemImageService.addImage(10L, 1L, file, false);

        assertThat(saved.isMainImage()).isFalse();
        assertThat(saved.getStoredFileName()).isEqualTo("stored.png");
        assertThat(saved.getImagePath()).isEqualTo("/uploads/stored.png");
        verify(itemImageRepository, never()).findByItemAndMainImageTrue(any());
    }

    @Test
    void addImage_asMain_unsetsPreviousMainImage() {
        Item item = createItem(1L);
        MultipartFile file = createFile();
        ItemImage existingMain = createImage(item, true);
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));
        when(itemImageRepository.findByItemAndMainImageTrue(item)).thenReturn(Optional.of(existingMain));
        when(fileStorageService.store(file)).thenReturn("stored.png");
        when(itemImageRepository.save(any(ItemImage.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ItemImage saved = itemImageService.addImage(10L, 1L, file, true);

        assertThat(existingMain.isMainImage()).isFalse();
        assertThat(saved.isMainImage()).isTrue();
    }

    @Test
    void addImage_notOwner_throws() {
        Item item = createItem(1L);
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> itemImageService.addImage(10L, 2L, createFile(), false))
                .isInstanceOf(IllegalStateException.class);
        verify(itemImageRepository, never()).save(any());
    }

    @Test
    void changeMainImage_switchesMainFlag() {
        Item item = createItem(1L);
        ItemImage oldMain = createImage(item, true);
        ItemImage newMain = createImage(item, false);
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));
        when(itemImageRepository.findByItemAndMainImageTrue(item)).thenReturn(Optional.of(oldMain));
        when(itemImageRepository.findById(20L)).thenReturn(Optional.of(newMain));

        itemImageService.changeMainImage(10L, 20L, 1L);

        assertThat(oldMain.isMainImage()).isFalse();
        assertThat(newMain.isMainImage()).isTrue();
    }

    @Test
    void changeMainImage_notOwner_throws() {
        Item item = createItem(1L);
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> itemImageService.changeMainImage(10L, 20L, 2L))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void deleteImage_asOwner_deletes() {
        Item item = createItem(1L);
        ItemImage image = createImage(item, false);
        when(itemImageRepository.findById(20L)).thenReturn(Optional.of(image));

        itemImageService.deleteImage(20L, 1L);

        verify(itemImageRepository).delete(image);
    }

    @Test
    void deleteImage_notOwner_throws() {
        Item item = createItem(1L);
        ItemImage image = createImage(item, false);
        when(itemImageRepository.findById(20L)).thenReturn(Optional.of(image));

        assertThatThrownBy(() -> itemImageService.deleteImage(20L, 2L))
                .isInstanceOf(IllegalStateException.class);
        verify(itemImageRepository, never()).delete(any());
    }

    @Test
    void getImages_delegatesToRepository() {
        Item item = createItem(1L);
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));
        List<ItemImage> images = List.of();
        when(itemImageRepository.findByItem(item)).thenReturn(images);

        assertThat(itemImageService.getImages(10L)).isSameAs(images);
    }

    private Member createMember(Long memberNo) {
        Member member = Member.builder()
                .email("owner@billage.com")
                .password("encoded")
                .name("소유자")
                .nickname("owner")
                .phone("010-1111-2222")
                .role(MemberRole.USER)
                .build();
        ReflectionTestUtils.setField(member, "memberNo", memberNo);
        return member;
    }

    private Item createItem(Long ownerMemberNo) {
        return Item.builder()
                .member(createMember(ownerMemberNo))
                .category(Category.TOOL)
                .itemName("전동드릴")
                .description("거의 새것")
                .rentalPoint(1000)
                .rentalPlaceName("우리집 앞")
                .rentalPlace("서울시 강남구")
                .rentalPlaceDetail("1층")
                .latitude(37.5)
                .longitude(127.0)
                .build();
    }

    private ItemImage createImage(Item item, boolean mainImage) {
        return ItemImage.builder()
                .item(item)
                .originalFileName("origin.png")
                .storedFileName("stored.png")
                .imagePath("/path")
                .mainImage(mainImage)
                .build();
    }

    private MultipartFile createFile() {
        return new MockMultipartFile("file", "origin.png", "image/png", "content".getBytes());
    }
}
