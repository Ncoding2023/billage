package com.sping.billage.domain.item.repository;

import com.sping.billage.config.JpaAuditingConfig;
import com.sping.billage.domain.item.entity.Item;
import com.sping.billage.domain.item.entity.ItemImage;
import com.sping.billage.domain.item.enums.ItemCategory;
import com.sping.billage.domain.member.entity.Member;
import com.sping.billage.domain.member.enums.MemberRole;
import com.sping.billage.domain.member.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.UUID;

@Slf4j
@DataJpaTest
@Import(JpaAuditingConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ItemImageRepositoryTest {

    @Autowired
    private ItemImageRepository itemImageRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private MemberRepository memberRepository;

    @Test
    void testSaveAndRead() {
        Member owner = memberRepository.saveAndFlush(member("owner"));
        Item item = itemRepository.saveAndFlush(Item.builder()
                .name("이미지 테스트 물품")
                .rentalPoint(1000L)
                .category(ItemCategory.TOOL)
                .owner(owner)
                .build());

        itemImageRepository.saveAndFlush(image(item, "a.png"));
        itemImageRepository.saveAndFlush(image(item, "b.png"));

        List<ItemImage> images = itemImageRepository.findByItemIdOrderByIdAsc(item.getId());
        log.info("이미지 개수: {}", images.size());
        images.forEach(image -> log.info("이미지: id={}, original={}, path={}",
                image.getId(), image.getOriginalFileName(), image.getImagePath()));

        itemImageRepository.deleteByItemId(item.getId());
        itemImageRepository.flush();

        List<ItemImage> afterDelete = itemImageRepository.findByItemIdOrderByIdAsc(item.getId());
        log.info("삭제 후 이미지 개수: {}", afterDelete.size());
    }

    private ItemImage image(Item item, String fileName) {
        return ItemImage.builder()
                .originalFileName(fileName)
                .storedFileName("stored-" + fileName)
                .imagePath("/upload/" + fileName)
                .item(item)
                .build();
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
