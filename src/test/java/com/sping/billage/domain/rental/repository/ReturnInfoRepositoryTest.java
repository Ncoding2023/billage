package com.sping.billage.domain.rental.repository;

import com.sping.billage.config.JpaAuditingConfig;
import com.sping.billage.domain.item.entity.Item;
import com.sping.billage.domain.item.enums.ItemCategory;
import com.sping.billage.domain.item.repository.ItemRepository;
import com.sping.billage.domain.member.entity.Member;
import com.sping.billage.domain.member.enums.MemberRole;
import com.sping.billage.domain.member.repository.MemberRepository;
import com.sping.billage.domain.rental.entity.Rental;
import com.sping.billage.domain.rental.entity.ReturnInfo;
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
class ReturnInfoRepositoryTest {

    @Autowired
    private ReturnInfoRepository returnInfoRepository;
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
                .name("반납 테스트 물품")
                .rentalPoint(500L)
                .category(ItemCategory.ETC)
                .owner(owner)
                .build());
        Rental rental = rentalRepository.saveAndFlush(Rental.builder()
                .startDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusDays(3))
                .renter(renter)
                .item(item)
                .build());

        returnInfoRepository.saveAndFlush(ReturnInfo.builder()
                .memo("깨끗하게 사용했습니다")
                .rental(rental)
                .build());

        ReturnInfo returnInfo = returnInfoRepository.findByRentalId(rental.getId())
                .orElseThrow(() -> new IllegalArgumentException("반납 정보가 존재하지 않습니다."));

        log.info("===== 반납 정보 =====");
        log.info("id: {}", returnInfo.getId());
        log.info("status: {}", returnInfo.getStatus());
        log.info("memo: {}", returnInfo.getMemo());
        log.info("returnedAt: {}", returnInfo.getReturnedAt());
        log.info("createdAt: {}", returnInfo.getCreatedAt());
        log.info("존재 여부: {}", returnInfoRepository.existsByRentalId(rental.getId()));

        returnInfo.complete();
        returnInfoRepository.saveAndFlush(returnInfo);
        log.info("확인 후 상태: {}", returnInfo.getStatus());
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
