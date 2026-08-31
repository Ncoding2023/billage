package com.travel.billage.domain.rental;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.travel.billage.domain.category.Category;
import com.travel.billage.domain.item.Item;
import com.travel.billage.domain.item.ItemRepository;
import com.travel.billage.domain.item.ItemStatus;
import com.travel.billage.domain.member.Member;
import com.travel.billage.domain.member.MemberRepository;
import com.travel.billage.domain.member.MemberRole;
import com.travel.billage.domain.point.PointService;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class RentalServiceTest {

    @Mock
    private RentalRepository rentalRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private ItemReturnRepository itemReturnRepository;
    @Mock
    private PointService pointService;

    @InjectMocks
    private RentalService rentalService;

    private final LocalDate start = LocalDate.now().plusDays(1);
    private final LocalDate end = LocalDate.now().plusDays(3);

    @Test
    void requestRental_success() {
        Member owner = createMember(2L);
        Member renter = createMember(1L);
        Item item = createItem(owner, ItemStatus.AVAILABLE);
        when(memberRepository.findById(1L)).thenReturn(Optional.of(renter));
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));
        when(rentalRepository.existsOverlappingRental(item, start, end)).thenReturn(false);
        when(rentalRepository.save(any(Rental.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Rental rental = rentalService.requestRental(1L, 10L, start, end);

        assertThat(rental.getMember()).isEqualTo(renter);
        assertThat(rental.getItem()).isEqualTo(item);
        assertThat(rental.getRentalPoint()).isEqualTo(1000);
        assertThat(rental.getRentalStatus()).isEqualTo(RentalStatus.REQUESTED);
        verify(pointService).chargeForRental(renter, rental);
        verify(pointService).payoutForRental(owner, rental);
    }

    @Test
    void requestRental_itemNotAvailable_throws() {
        Member owner = createMember(2L);
        Member renter = createMember(1L);
        Item item = createItem(owner, ItemStatus.UNAVAILABLE);
        when(memberRepository.findById(1L)).thenReturn(Optional.of(renter));
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> rentalService.requestRental(1L, 10L, start, end))
                .isInstanceOf(IllegalStateException.class);

        verify(rentalRepository, never()).save(any());
        verify(pointService, never()).chargeForRental(any(), any());
    }

    @Test
    void requestRental_overlappingPeriod_throws() {
        Member owner = createMember(2L);
        Member renter = createMember(1L);
        Item item = createItem(owner, ItemStatus.AVAILABLE);
        when(memberRepository.findById(1L)).thenReturn(Optional.of(renter));
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));
        when(rentalRepository.existsOverlappingRental(item, start, end)).thenReturn(true);

        assertThatThrownBy(() -> rentalService.requestRental(1L, 10L, start, end))
                .isInstanceOf(IllegalStateException.class);

        verify(rentalRepository, never()).save(any());
    }

    @Test
    void requestRental_memberNotFound_throws() {
        when(memberRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> rentalService.requestRental(999L, 10L, start, end))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void requestRental_itemNotFound_throws() {
        Member renter = createMember(1L);
        when(memberRepository.findById(1L)).thenReturn(Optional.of(renter));
        when(itemRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> rentalService.requestRental(1L, 999L, start, end))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void startRental_asProvider_changesStatusToRenting() {
        Member owner = createMember(2L);
        Rental rental = createRental(owner, createMember(1L), RentalStatus.REQUESTED);
        when(rentalRepository.findById(100L)).thenReturn(Optional.of(rental));

        rentalService.startRental(100L, 2L);

        assertThat(rental.getRentalStatus()).isEqualTo(RentalStatus.RENTING);
    }

    @Test
    void startRental_notProvider_throws() {
        Member owner = createMember(2L);
        Rental rental = createRental(owner, createMember(1L), RentalStatus.REQUESTED);
        when(rentalRepository.findById(100L)).thenReturn(Optional.of(rental));

        assertThatThrownBy(() -> rentalService.startRental(100L, 999L))
                .isInstanceOf(IllegalStateException.class);
        assertThat(rental.getRentalStatus()).isEqualTo(RentalStatus.REQUESTED);
    }

    @Test
    void startRental_wrongStatus_throws() {
        Member owner = createMember(2L);
        Rental rental = createRental(owner, createMember(1L), RentalStatus.RENTING);
        when(rentalRepository.findById(100L)).thenReturn(Optional.of(rental));

        assertThatThrownBy(() -> rentalService.startRental(100L, 2L))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void cancelRental_asRenter_refundsBothSides() {
        Member owner = createMember(2L);
        Member renter = createMember(1L);
        Rental rental = createRental(owner, renter, RentalStatus.REQUESTED);
        when(rentalRepository.findById(100L)).thenReturn(Optional.of(rental));

        rentalService.cancelRental(100L, 1L);

        assertThat(rental.getRentalStatus()).isEqualTo(RentalStatus.CANCELED);
        verify(pointService).refundForRental(renter, rental);
        verify(pointService).reverseForRental(owner, rental);
    }

    @Test
    void cancelRental_notRenter_throws() {
        Member owner = createMember(2L);
        Member renter = createMember(1L);
        Rental rental = createRental(owner, renter, RentalStatus.REQUESTED);
        when(rentalRepository.findById(100L)).thenReturn(Optional.of(rental));

        assertThatThrownBy(() -> rentalService.cancelRental(100L, 999L))
                .isInstanceOf(IllegalStateException.class);
        verify(pointService, never()).refundForRental(any(), any());
    }

    @Test
    void cancelRental_wrongStatus_throws() {
        Member owner = createMember(2L);
        Member renter = createMember(1L);
        Rental rental = createRental(owner, renter, RentalStatus.RENTING);
        when(rentalRepository.findById(100L)).thenReturn(Optional.of(rental));

        assertThatThrownBy(() -> rentalService.cancelRental(100L, 1L))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void requestReturn_asRenter_createsReturnAndChangesStatus() {
        Member owner = createMember(2L);
        Member renter = createMember(1L);
        Rental rental = createRental(owner, renter, RentalStatus.RENTING);
        when(rentalRepository.findById(100L)).thenReturn(Optional.of(rental));

        rentalService.requestReturn(100L, 1L);

        assertThat(rental.getRentalStatus()).isEqualTo(RentalStatus.RETURN_PENDING);
        verify(itemReturnRepository).save(any(ItemReturn.class));
    }

    @Test
    void requestReturn_notRenter_throws() {
        Member owner = createMember(2L);
        Member renter = createMember(1L);
        Rental rental = createRental(owner, renter, RentalStatus.RENTING);
        when(rentalRepository.findById(100L)).thenReturn(Optional.of(rental));

        assertThatThrownBy(() -> rentalService.requestReturn(100L, 999L))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void requestReturn_wrongStatus_throws() {
        Member owner = createMember(2L);
        Member renter = createMember(1L);
        Rental rental = createRental(owner, renter, RentalStatus.REQUESTED);
        when(rentalRepository.findById(100L)).thenReturn(Optional.of(rental));

        assertThatThrownBy(() -> rentalService.requestReturn(100L, 1L))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void confirmReturn_asProvider_completesRental() {
        Member owner = createMember(2L);
        Member renter = createMember(1L);
        Rental rental = createRental(owner, renter, RentalStatus.RETURN_PENDING);
        ItemReturn itemReturn = ItemReturn.builder()
                .rental(rental)
                .returnDate(java.time.LocalDateTime.now())
                .returnStatus(ReturnStatus.NORMAL)
                .build();
        when(rentalRepository.findById(100L)).thenReturn(Optional.of(rental));
        when(itemReturnRepository.findByRental(rental)).thenReturn(Optional.of(itemReturn));

        rentalService.confirmReturn(100L, 2L, ReturnStatus.DAMAGED, "파손됨");

        assertThat(rental.getRentalStatus()).isEqualTo(RentalStatus.RETURN_COMPLETED);
        assertThat(itemReturn.getReturnStatus()).isEqualTo(ReturnStatus.DAMAGED);
        assertThat(itemReturn.getReturnMemo()).isEqualTo("파손됨");
    }

    @Test
    void confirmReturn_notProvider_throws() {
        Member owner = createMember(2L);
        Member renter = createMember(1L);
        Rental rental = createRental(owner, renter, RentalStatus.RETURN_PENDING);
        when(rentalRepository.findById(100L)).thenReturn(Optional.of(rental));

        assertThatThrownBy(() -> rentalService.confirmReturn(100L, 999L, ReturnStatus.NORMAL, null))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void confirmReturn_wrongStatus_throws() {
        Member owner = createMember(2L);
        Member renter = createMember(1L);
        Rental rental = createRental(owner, renter, RentalStatus.RENTING);
        when(rentalRepository.findById(100L)).thenReturn(Optional.of(rental));

        assertThatThrownBy(() -> rentalService.confirmReturn(100L, 2L, ReturnStatus.NORMAL, null))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void confirmReturn_noReturnRecord_throws() {
        Member owner = createMember(2L);
        Member renter = createMember(1L);
        Rental rental = createRental(owner, renter, RentalStatus.RETURN_PENDING);
        when(rentalRepository.findById(100L)).thenReturn(Optional.of(rental));
        when(itemReturnRepository.findByRental(rental)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> rentalService.confirmReturn(100L, 2L, ReturnStatus.NORMAL, null))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void getRentalsAsRenter_delegatesToRepository() {
        Member renter = createMember(1L);
        when(memberRepository.findById(1L)).thenReturn(Optional.of(renter));
        List<Rental> rentals = List.of();
        when(rentalRepository.findByMember(renter)).thenReturn(rentals);

        assertThat(rentalService.getRentalsAsRenter(1L)).isSameAs(rentals);
    }

    @Test
    void getRentalsAsProvider_delegatesToRepository() {
        Member owner = createMember(2L);
        when(memberRepository.findById(2L)).thenReturn(Optional.of(owner));
        List<Rental> rentals = List.of();
        when(rentalRepository.findByItem_Member(owner)).thenReturn(rentals);

        assertThat(rentalService.getRentalsAsProvider(2L)).isSameAs(rentals);
    }

    private Member createMember(Long memberNo) {
        Member member = Member.builder()
                .email("member" + memberNo + "@billage.com")
                .password("encoded")
                .name("회원" + memberNo)
                .nickname("member" + memberNo)
                .phone("010-1111-2222")
                .role(MemberRole.USER)
                .build();
        ReflectionTestUtils.setField(member, "memberNo", memberNo);
        return member;
    }

    private Item createItem(Member owner, ItemStatus status) {
        Item item = Item.builder()
                .member(owner)
                .category(Category.TOOL)
                .itemName("전동드릴")
                .description("거의 새것")
                .rentalPoint(1000)
                .rentalPlaceName("우리집")
                .rentalPlace("서울시")
                .rentalPlaceDetail(null)
                .latitude(37.0)
                .longitude(127.0)
                .build();
        item.changeStatus(status);
        return item;
    }

    private Rental createRental(Member owner, Member renter, RentalStatus status) {
        Item item = createItem(owner, ItemStatus.AVAILABLE);
        Rental rental = Rental.builder()
                .item(item)
                .member(renter)
                .rentalStartDate(start)
                .rentalEndDate(end)
                .rentalPoint(1000)
                .build();
        rental.changeStatus(status);
        return rental;
    }
}
