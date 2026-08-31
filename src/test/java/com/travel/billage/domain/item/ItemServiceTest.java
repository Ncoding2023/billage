package com.travel.billage.domain.item;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.travel.billage.domain.category.Category;
import com.travel.billage.domain.member.Member;
import com.travel.billage.domain.member.MemberRepository;
import com.travel.billage.domain.member.MemberRole;
import com.travel.billage.domain.rental.RentalRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private RentalRepository rentalRepository;

    @InjectMocks
    private ItemService itemService;

    @Test
    void registerItem_success() {
        Member owner = createMember(1L);
        when(memberRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Item item = itemService.registerItem(1L, Category.TOOL, "전동드릴", "거의 새것", 1000,
                "우리집 앞", "서울시 강남구", "1층", 37.5, 127.0);

        assertThat(item.getMember()).isEqualTo(owner);
        assertThat(item.getCategory()).isEqualTo(Category.TOOL);
        assertThat(item.getItemName()).isEqualTo("전동드릴");
        assertThat(item.getRentalPoint()).isEqualTo(1000);
        assertThat(item.getRentalPlaceName()).isEqualTo("우리집 앞");
        assertThat(item.getRentalPlace()).isEqualTo("서울시 강남구");
        assertThat(item.getLatitude()).isEqualTo(37.5);
        assertThat(item.getLongitude()).isEqualTo(127.0);
        assertThat(item.getItemStatus()).isEqualTo(ItemStatus.AVAILABLE);
    }

    @Test
    void registerItem_memberNotFound_throws() {
        when(memberRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.registerItem(999L, Category.TOOL, "드릴", "설명", 1000,
                "장소", "주소", null, 37.0, 127.0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getItem_notFound_throws() {
        when(itemRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.getItem(999L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void updateItem_asOwner_updatesFields() {
        Member owner = createMember(1L);
        Item item = createItem(owner);
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));

        itemService.updateItem(10L, 1L, Category.CLOTHES, "정장", "새 설명", 2000,
                "새 장소", "새 주소", "2층", 38.0, 128.0);

        assertThat(item.getCategory()).isEqualTo(Category.CLOTHES);
        assertThat(item.getItemName()).isEqualTo("정장");
        assertThat(item.getRentalPoint()).isEqualTo(2000);
        assertThat(item.getRentalPlaceName()).isEqualTo("새 장소");
        assertThat(item.getLatitude()).isEqualTo(38.0);
    }

    @Test
    void updateItem_notOwner_throws() {
        Member owner = createMember(1L);
        Item item = createItem(owner);
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> itemService.updateItem(10L, 2L, Category.CLOTHES, "정장", "설명", 2000,
                "장소", "주소", null, 37.0, 127.0))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void changeStatus_asOwner_updatesStatus() {
        Member owner = createMember(1L);
        Item item = createItem(owner);
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));

        itemService.changeStatus(10L, 1L, ItemStatus.UNAVAILABLE);

        assertThat(item.getItemStatus()).isEqualTo(ItemStatus.UNAVAILABLE);
    }

    @Test
    void changeStatus_notOwner_throws() {
        Member owner = createMember(1L);
        Item item = createItem(owner);
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> itemService.changeStatus(10L, 2L, ItemStatus.UNAVAILABLE))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void deleteItem_asOwner_deletes() {
        Member owner = createMember(1L);
        Item item = createItem(owner);
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));
        when(rentalRepository.existsByItem(item)).thenReturn(false);

        itemService.deleteItem(10L, 1L);

        verify(itemRepository).delete(item);
    }

    @Test
    void deleteItem_notOwner_throws() {
        Member owner = createMember(1L);
        Item item = createItem(owner);
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> itemService.deleteItem(10L, 2L))
                .isInstanceOf(IllegalStateException.class);
        verify(itemRepository, never()).delete(any());
    }

    @Test
    void deleteItem_withExistingRentals_throws() {
        Member owner = createMember(1L);
        Item item = createItem(owner);
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));
        when(rentalRepository.existsByItem(item)).thenReturn(true);

        assertThatThrownBy(() -> itemService.deleteItem(10L, 1L))
                .isInstanceOf(IllegalStateException.class);
        verify(itemRepository, never()).delete(any());
    }

    @Test
    void adminChangeStatus_updatesStatusRegardlessOfOwner() {
        Member owner = createMember(1L);
        Item item = createItem(owner);
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));

        itemService.adminChangeStatus(10L, ItemStatus.UNAVAILABLE);

        assertThat(item.getItemStatus()).isEqualTo(ItemStatus.UNAVAILABLE);
    }

    @Test
    void adminDeleteItem_withoutRentals_deletes() {
        Member owner = createMember(1L);
        Item item = createItem(owner);
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));
        when(rentalRepository.existsByItem(item)).thenReturn(false);

        itemService.adminDeleteItem(10L);

        verify(itemRepository).delete(item);
    }

    @Test
    void adminDeleteItem_withRentals_throws() {
        Member owner = createMember(1L);
        Item item = createItem(owner);
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));
        when(rentalRepository.existsByItem(item)).thenReturn(true);

        assertThatThrownBy(() -> itemService.adminDeleteItem(10L))
                .isInstanceOf(IllegalStateException.class);
        verify(itemRepository, never()).delete(any());
    }

    @Test
    void getAllItems_delegatesToRepository() {
        List<Item> items = List.of();
        when(itemRepository.findAll()).thenReturn(items);

        assertThat(itemService.getAllItems()).isSameAs(items);
    }

    @Test
    void getItemsByCategory_delegatesToRepository() {
        List<Item> items = List.of();
        when(itemRepository.findByCategory(Category.TOOL)).thenReturn(items);

        assertThat(itemService.getItemsByCategory(Category.TOOL)).isSameAs(items);
    }

    @Test
    void searchItems_delegatesToRepository() {
        List<Item> items = List.of();
        when(itemRepository.findByItemNameContaining("드릴")).thenReturn(items);

        assertThat(itemService.searchItems("드릴")).isSameAs(items);
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

    private Item createItem(Member owner) {
        return Item.builder()
                .member(owner)
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
}
