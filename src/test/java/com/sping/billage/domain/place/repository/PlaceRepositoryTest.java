package com.sping.billage.domain.place.repository;

import com.sping.billage.config.JpaAuditingConfig;
import com.sping.billage.domain.item.entity.Item;
import com.sping.billage.domain.item.enums.ItemCategory;
import com.sping.billage.domain.item.repository.ItemRepository;
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

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@DataJpaTest
@Import(JpaAuditingConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PlaceRepositoryTest {

    @Autowired
    private PlaceRepository placeRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private MemberRepository memberRepository;

    @Test
    void testSaveAndRead() {
        Member owner = memberRepository.saveAndFlush(member("owner"));
        Item item = itemRepository.saveAndFlush(Item.builder()
                .name("장소 테스트 물품")
                .rentalPoint(1000L)
                .category(ItemCategory.LIVING)
                .owner(owner)
                .build());

        placeRepository.saveAndFlush(Place.builder()
                .address("서울 강남구")
                .detailAddress("101호")
                .latitude(new BigDecimal("37.5012743"))
                .longitude(new BigDecimal("127.0395850"))
                .item(item)
                .build());

        Place place = placeRepository.findByItemId(item.getId())
                .orElseThrow(() -> new IllegalArgumentException("장소가 존재하지 않습니다."));

        log.info("===== 장소 정보 =====");
        log.info("id: {}", place.getId());
        log.info("address: {}", place.getAddress());
        log.info("detailAddress: {}", place.getDetailAddress());
        log.info("latitude: {}", place.getLatitude());
        log.info("longitude: {}", place.getLongitude());
        log.info("itemId: {}", place.getItem().getId());
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
