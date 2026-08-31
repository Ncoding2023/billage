package com.travel.billage.domain.rental;

import static org.assertj.core.api.Assertions.assertThat;

import com.travel.billage.domain.category.Category;
import com.travel.billage.domain.item.Item;
import com.travel.billage.domain.item.ItemRepository;
import com.travel.billage.domain.member.Member;
import com.travel.billage.domain.member.MemberRepository;
import com.travel.billage.domain.member.MemberRole;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

@DataJpaTest
class RentalRepositoryTest {

    @Autowired
    private RentalRepository rentalRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private MemberRepository memberRepository;

    private static final LocalDate DAY1 = LocalDate.of(2026, 3, 1);
    private static final LocalDate DAY3 = LocalDate.of(2026, 3, 3);
    private static final LocalDate DAY5 = LocalDate.of(2026, 3, 5);

    @Test
    void findByMember_returnsRentalsForRenter() {
        Member owner = saveMember("owner");
        Member renter = saveMember("renter");
        Item item = saveItem(owner);
        rentalRepository.save(createRental(item, renter, DAY1, DAY3, RentalStatus.REQUESTED));

        List<Rental> rentals = rentalRepository.findByMember(renter);

        assertThat(rentals).hasSize(1);
        assertThat(rentals.get(0).getMember()).isEqualTo(renter);
    }

    @Test
    void findByItem_returnsRentalsForItem() {
        Member owner = saveMember("owner");
        Member renter = saveMember("renter");
        Item item = saveItem(owner);
        rentalRepository.save(createRental(item, renter, DAY1, DAY3, RentalStatus.REQUESTED));

        assertThat(rentalRepository.findByItem(item)).hasSize(1);
    }

    @Test
    void findByItemMember_returnsRentalsForProvider() {
        Member owner = saveMember("owner");
        Member renter = saveMember("renter");
        Item item = saveItem(owner);
        rentalRepository.save(createRental(item, renter, DAY1, DAY3, RentalStatus.REQUESTED));

        List<Rental> rentals = rentalRepository.findByItem_Member(owner);

        assertThat(rentals).hasSize(1);
        assertThat(rentals.get(0).getItem().getMember()).isEqualTo(owner);
    }

    @Test
    void existsOverlappingRental_trueWhenPeriodsOverlap() {
        Member owner = saveMember("owner");
        Member renter = saveMember("renter");
        Item item = saveItem(owner);
        rentalRepository.save(createRental(item, renter, DAY3, DAY5, RentalStatus.REQUESTED));

        boolean overlap = rentalRepository.existsOverlappingRental(item, DAY3.plusDays(1), DAY5.plusDays(1));

        assertThat(overlap).isTrue();
    }

    @Test
    void existsOverlappingRental_falseWhenPeriodsDontOverlap() {
        Member owner = saveMember("owner");
        Member renter = saveMember("renter");
        Item item = saveItem(owner);
        rentalRepository.save(createRental(item, renter, DAY3, DAY5, RentalStatus.REQUESTED));

        boolean overlap = rentalRepository.existsOverlappingRental(item, DAY5.plusDays(3), DAY5.plusDays(5));

        assertThat(overlap).isFalse();
    }

    @Test
    void existsOverlappingRental_trueWhenBoundaryDatesTouch() {
        Member owner = saveMember("owner");
        Member renter = saveMember("renter");
        Item item = saveItem(owner);
        rentalRepository.save(createRental(item, renter, DAY3, DAY5, RentalStatus.REQUESTED));

        boolean overlap = rentalRepository.existsOverlappingRental(item, DAY5, DAY5.plusDays(2));

        assertThat(overlap).isTrue();
    }

    @Test
    void existsOverlappingRental_falseWhenOnlyCanceledRentalOverlaps() {
        Member owner = saveMember("owner");
        Member renter = saveMember("renter");
        Item item = saveItem(owner);
        rentalRepository.save(createRental(item, renter, DAY3, DAY5, RentalStatus.CANCELED));

        boolean overlap = rentalRepository.existsOverlappingRental(item, DAY3, DAY5);

        assertThat(overlap).isFalse();
    }

    private Member saveMember(String nickname) {
        return memberRepository.save(Member.builder()
                .email(nickname + "@billage.com")
                .password("encoded")
                .name(nickname)
                .nickname(nickname)
                .phone("010-1111-2222")
                .role(MemberRole.USER)
                .build());
    }

    private Item saveItem(Member owner) {
        return itemRepository.save(Item.builder()
                .member(owner)
                .category(Category.TOOL)
                .itemName("전동드릴")
                .description("설명")
                .rentalPoint(1000)
                .rentalPlaceName("우리집")
                .rentalPlace("서울시")
                .rentalPlaceDetail(null)
                .latitude(37.0)
                .longitude(127.0)
                .build());
    }

    private Rental createRental(Item item, Member renter, LocalDate start, LocalDate end, RentalStatus status) {
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
