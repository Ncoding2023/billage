package com.travel.billage.domain.rental;

import static org.assertj.core.api.Assertions.assertThat;

import com.travel.billage.domain.category.Category;
import com.travel.billage.domain.item.Item;
import com.travel.billage.domain.item.ItemRepository;
import com.travel.billage.domain.member.Member;
import com.travel.billage.domain.member.MemberRepository;
import com.travel.billage.domain.member.MemberRole;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

@DataJpaTest
class ItemReturnRepositoryTest {

    @Autowired
    private ItemReturnRepository itemReturnRepository;
    @Autowired
    private RentalRepository rentalRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private MemberRepository memberRepository;

    @Test
    void findByRental_returnsSavedReturnInfo() {
        Rental rental = saveRental();
        itemReturnRepository.save(ItemReturn.builder()
                .rental(rental)
                .returnDate(LocalDateTime.now())
                .returnStatus(ReturnStatus.NORMAL)
                .returnMemo("정상 반납")
                .build());

        Optional<ItemReturn> found = itemReturnRepository.findByRental(rental);

        assertThat(found).isPresent();
        assertThat(found.get().getReturnMemo()).isEqualTo("정상 반납");
    }

    @Test
    void findByRental_emptyWhenNoReturnYet() {
        Rental rental = saveRental();

        assertThat(itemReturnRepository.findByRental(rental)).isEmpty();
    }

    private Rental saveRental() {
        Member owner = memberRepository.save(Member.builder()
                .email("owner@billage.com")
                .password("encoded")
                .name("소유자")
                .nickname("owner")
                .phone("010-1111-2222")
                .role(MemberRole.USER)
                .build());
        Member renter = memberRepository.save(Member.builder()
                .email("renter@billage.com")
                .password("encoded")
                .name("이용자")
                .nickname("renter")
                .phone("010-2222-3333")
                .role(MemberRole.USER)
                .build());
        Item item = itemRepository.save(Item.builder()
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
        return rentalRepository.save(Rental.builder()
                .item(item)
                .member(renter)
                .rentalStartDate(LocalDate.now())
                .rentalEndDate(LocalDate.now().plusDays(3))
                .rentalPoint(1000)
                .build());
    }
}
