package com.sping.billage.domain.item.repository;

import com.sping.billage.config.JpaAuditingConfig;
import com.sping.billage.domain.item.entity.Item;
import com.sping.billage.domain.item.entity.ItemImage;
import com.sping.billage.domain.item.enums.ItemCategory;
import com.sping.billage.domain.member.entity.Member;
import com.sping.billage.domain.member.enums.MemberRole;
import com.sping.billage.domain.member.repository.MemberRepository;
import com.sping.billage.domain.place.entity.Place;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@DataJpaTest
@Import(JpaAuditingConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private MemberRepository memberRepository;

    @Test
    void testSaveAndRead() {
        Member owner = memberRepository.saveAndFlush(member("owner"));

        Item item = Item.builder()
                .name("?? ??")
                .description("?? ? ?????")
                .rentalPoint(3000L)
                .category(ItemCategory.TOOL)
                .thumbnailPath("/upload/thumb.png")
                .owner(owner)
                .build();

        item.assignPlace(Place.builder()
                .address("?? ???")
                .detailAddress("101?")
                .latitude(new BigDecimal("37.5012743"))
                .longitude(new BigDecimal("127.0395850"))
                .build());

        item.addImage(ItemImage.builder()
                .originalFileName("detail.png")
                .storedFileName("detail-stored.png")
                .imagePath("/upload/detail.png")
                .build());

        Item saved = itemRepository.saveAndFlush(item);
        log.info("??? ?? ID: {}", saved.getId());

        Item found = itemRepository.findWithOwnerAndPlaceById(saved.getId())
                .orElseThrow(() -> new IllegalArgumentException("??? ???? ????."));

        printItem(found);

        Page<Item> searchResult = itemRepository.search(
                ItemCategory.TOOL, null, "??", PageRequest.of(0, 10));
        log.info("?? ?? ??: {}", searchResult.getTotalElements());
        searchResult.forEach(result -> log.info("??? ??: id={}, name={}", result.getId(), result.getName()));
    }

    private void printItem(Item item) {
        log.info("===== ?? ?? =====");
        log.info("id: {}", item.getId());
        log.info("name: {}", item.getName());
        log.info("description: {}", item.getDescription());
        log.info("rentalPoint: {}", item.getRentalPoint());
        log.info("status: {}", item.getStatus());
        log.info("category: {}", item.getCategory());
        log.info("owner: {}", item.getOwner().getNickname());
        log.info("place: {}", item.getPlace() == null ? null : item.getPlace().getAddress());
        log.info("imageCount: {}", item.getImages().size());
        log.info("createdAt: {}", item.getCreatedAt());
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
