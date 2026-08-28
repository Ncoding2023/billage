package com.sping.billage.domain.item.service;

import com.sping.billage.domain.item.dto.ItemUpdateRequest;
import com.sping.billage.domain.item.entity.Item;
import com.sping.billage.domain.item.enums.ItemCategory;
import com.sping.billage.domain.item.enums.ItemStatus;
import com.sping.billage.domain.item.mapper.ItemMapper;
import com.sping.billage.domain.item.repository.ItemRepository;
import com.sping.billage.domain.member.entity.Member;
import com.sping.billage.domain.member.enums.MemberRole;
import com.sping.billage.domain.member.repository.MemberRepository;
import com.sping.billage.domain.rental.enums.RentalStatus;
import com.sping.billage.domain.rental.repository.RentalRepository;
import com.sping.billage.global.exception.BusinessException;
import com.sping.billage.global.exception.ErrorCode;
import com.sping.billage.global.file.FileStorageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    private static final Long OWNER_ID = 1L;
    private static final Long OTHER_MEMBER_ID = 2L;
    private static final Long ITEM_ID = 10L;

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private RentalRepository rentalRepository;
    @Mock
    private ItemMapper itemMapper;
    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private ItemService itemService;

    @Test
    @DisplayName("소유자가 아니면 물품을 수정할 수 없다")
    void updateItem_notOwner_throws() {
        given(itemRepository.findWithOwnerAndPlaceById(ITEM_ID)).willReturn(Optional.of(item()));

        assertThatThrownBy(() -> itemService.updateItem(
                OTHER_MEMBER_ID, ITEM_ID, emptyUpdateRequest(), null, null))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_ITEM_OWNER);

        verifyNoInteractions(fileStorageService);
    }

    @Test
    @DisplayName("소유자가 아니면 물품을 삭제할 수 없다")
    void deleteItem_notOwner_throws() {
        given(itemRepository.findWithOwnerAndPlaceById(ITEM_ID)).willReturn(Optional.of(item()));

        assertThatThrownBy(() -> itemService.deleteItem(OTHER_MEMBER_ID, ITEM_ID))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_ITEM_OWNER);

        verify(itemRepository, never()).delete(any());
    }

    @Test
    @DisplayName("대여 중인 물품은 삭제할 수 없다")
    void deleteItem_rented_throws() {
        Item item = item();
        item.changeStatus(ItemStatus.RENTED);
        given(itemRepository.findWithOwnerAndPlaceById(ITEM_ID)).willReturn(Optional.of(item));

        assertThatThrownBy(() -> itemService.deleteItem(OWNER_ID, ITEM_ID))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ITEM_DELETE_NOT_ALLOWED);

        verify(itemRepository, never()).delete(any());
    }

    @Test
    @DisplayName("진행 중인 대여가 있으면 삭제할 수 없다")
    void deleteItem_activeRentalExists_throws() {
        given(itemRepository.findWithOwnerAndPlaceById(ITEM_ID)).willReturn(Optional.of(item()));
        given(rentalRepository.existsByItemIdAndStatusIn(ITEM_ID, RentalStatus.activeStatuses()))
                .willReturn(true);

        assertThatThrownBy(() -> itemService.deleteItem(OWNER_ID, ITEM_ID))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ITEM_DELETE_NOT_ALLOWED);

        verify(itemRepository, never()).delete(any());
    }

    @Test
    @DisplayName("대여 이력이 남아 있으면 삭제할 수 없다")
    void deleteItem_rentalHistoryExists_throws() {
        given(itemRepository.findWithOwnerAndPlaceById(ITEM_ID)).willReturn(Optional.of(item()));
        given(rentalRepository.existsByItemIdAndStatusIn(ITEM_ID, RentalStatus.activeStatuses()))
                .willReturn(false);
        given(rentalRepository.existsByItemId(ITEM_ID)).willReturn(true);

        assertThatThrownBy(() -> itemService.deleteItem(OWNER_ID, ITEM_ID))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ITEM_DELETE_NOT_ALLOWED);

        verify(itemRepository, never()).delete(any());
    }

    @Test
    @DisplayName("소유자는 대여 이력이 없는 물품을 삭제할 수 있다")
    void deleteItem_success() {
        Item item = item();
        given(itemRepository.findWithOwnerAndPlaceById(ITEM_ID)).willReturn(Optional.of(item));
        given(rentalRepository.existsByItemIdAndStatusIn(ITEM_ID, RentalStatus.activeStatuses()))
                .willReturn(false);
        given(rentalRepository.existsByItemId(ITEM_ID)).willReturn(false);

        itemService.deleteItem(OWNER_ID, ITEM_ID);

        verify(itemRepository).delete(item);
        verify(fileStorageService).delete("/upload/thumb.png");
    }

    @Test
    @DisplayName("존재하지 않는 물품을 조회하면 예외가 발생한다")
    void getItem_notFound_throws() {
        given(itemRepository.findWithOwnerAndPlaceById(ITEM_ID)).willReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.getItem(ITEM_ID))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ITEM_NOT_FOUND);
    }

    @Test
    @DisplayName("대여 상태(RENTED)는 물품 수정 API로 지정할 수 없다")
    void updateItem_statusRented_throws() {
        given(itemRepository.findWithOwnerAndPlaceById(ITEM_ID)).willReturn(Optional.of(item()));
        given(fileStorageService.storeAll(null)).willReturn(java.util.List.of());

        ItemUpdateRequest request = new ItemUpdateRequest(
                null, null, null, null, ItemStatus.RENTED, null, null, null, null);

        assertThatThrownBy(() -> itemService.updateItem(OWNER_ID, ITEM_ID, request, null, null))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_INPUT_VALUE);
    }

    private ItemUpdateRequest emptyUpdateRequest() {
        return new ItemUpdateRequest(null, null, null, null, null, null, null, null, null);
    }

    private Item item() {
        Member owner = Member.builder()
                .email("owner@billage.com")
                .password("encoded")
                .nickname("소유자")
                .role(MemberRole.USER)
                .build();
        ReflectionTestUtils.setField(owner, "id", OWNER_ID);

        Item item = Item.builder()
                .name("전동 드릴")
                .description("설명")
                .rentalPoint(3000L)
                .category(ItemCategory.TOOL)
                .thumbnailPath("/upload/thumb.png")
                .owner(owner)
                .build();
        ReflectionTestUtils.setField(item, "id", ITEM_ID);
        return item;
    }
}
