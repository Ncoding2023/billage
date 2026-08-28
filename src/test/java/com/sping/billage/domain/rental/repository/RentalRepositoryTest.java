package com.sping.billage.domain.rental.repository;

import com.sping.billage.config.JpaAuditingConfig;
import com.sping.billage.domain.item.entity.Item;
import com.sping.billage.domain.item.enums.ItemCategory;
import com.sping.billage.domain.item.repository.ItemRepository;
import com.sping.billage.domain.member.entity.Member;
import com.sping.billage.domain.member.enums.MemberRole;
import com.sping.billage.domain.member.repository.MemberRepository;
import com.sping.billage.domain.rental.entity.Rental;
import com.sping.billage.domain.rental.enums.RentalStatus;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@DataJpaTest
@Import(JpaAuditingConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RentalRepositoryTest {

    @Autowired
    private RentalRepository rentalRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private MemberRepository memberRepository;

    @Test
    void testSaveAndRead() {
        Member owner = memberRepository.saveAndFlush(member("owner"));
        Member renter = memberRepository.saveAndFlush(member("renter"));
        Item item = itemRepository.saveAndFlush(Item.builder()
                .name("?? ??? ??")
                .rentalPoint(500L)
                .category(ItemCategory.ETC)
                .owner(owner)
                .build());

        Rental rental = rentalRepository.saveAndFlush(Rental.builder()
                .startDate(LocalDate.of(2026, 9, 1))
                .endDate(LocalDate.of(2026, 9, 5))
                .renter(renter)
                .item(item)
                .build());

        log.info("===== ?? ?? =====");
        log.info("id: {}", rental.getId());
        log.info("status: {}", rental.getStatus());
        log.info("startDate: {}", rental.getStartDate());
        log.info("endDate: {}", rental.getEndDate());
        log.info("renter: {}", rental.getRenter().getNickname());
        log.info("item: {}", rental.getItem().getName());

        rental.changeStatus(RentalStatus.APPROVED);
        rentalRepository.saveAndFlush(rental);

        boolean overlapped = rentalRepository.existsOverlappedRental(
                item.getId(),
                LocalDate.of(2026, 9, 3),
                LocalDate.of(2026, 9, 7),
                RentalStatus.occupyingStatuses());
        log.info("?? ?? ??(APPROVED): {}", overlapped);

        boolean requestedOnly = rentalRepository.existsOverlappedRental(
                item.getId(),
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 10, 3),
                RentalStatus.occupyingStatuses());
        log.info("?? ?? ?? ??: {}", requestedOnly);
    }

    private Member member(String prefix) {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        return Member.builder()
                .email(prefix + "-" + suffix + "@test.com")
                .password("encoded")
                .nickname(prefix + "-" + suffix)
                .role(MemberRole.USER)
                .build();
    }
}
