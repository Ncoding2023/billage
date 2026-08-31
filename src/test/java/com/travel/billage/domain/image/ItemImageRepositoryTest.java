package com.travel.billage.domain.image;

import static org.assertj.core.api.Assertions.assertThat;

import com.travel.billage.domain.category.Category;
import com.travel.billage.domain.item.Item;
import com.travel.billage.domain.item.ItemRepository;
import com.travel.billage.domain.member.Member;
import com.travel.billage.domain.member.MemberRepository;
import com.travel.billage.domain.member.MemberRole;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

@DataJpaTest
class ItemImageRepositoryTest {

    @Autowired
    private ItemImageRepository itemImageRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private MemberRepository memberRepository;

    @Test
    void findByItem_returnsAllImagesForItem() {
        Item item = createSavedItem();
        itemImageRepository.save(createImage(item, "a.png", false));
        itemImageRepository.save(createImage(item, "b.png", true));

        List<ItemImage> images = itemImageRepository.findByItem(item);

        assertThat(images).extracting(ItemImage::getOriginalFileName).containsExactlyInAnyOrder("a.png", "b.png");
    }

    @Test
    void findByItemAndMainImageTrue_returnsOnlyMainImage() {
        Item item = createSavedItem();
        itemImageRepository.save(createImage(item, "a.png", false));
        itemImageRepository.save(createImage(item, "b.png", true));

        Optional<ItemImage> main = itemImageRepository.findByItemAndMainImageTrue(item);

        assertThat(main).isPresent();
        assertThat(main.get().getOriginalFileName()).isEqualTo("b.png");
    }

    @Test
    void findByItemAndMainImageTrue_emptyWhenNoMainImage() {
        Item item = createSavedItem();
        itemImageRepository.save(createImage(item, "a.png", false));

        assertThat(itemImageRepository.findByItemAndMainImageTrue(item)).isEmpty();
    }

    private Item createSavedItem() {
        Member owner = memberRepository.save(Member.builder()
                .email("owner@billage.com")
                .password("encoded")
                .name("소유자")
                .nickname("owner")
                .phone("010-1111-2222")
                .role(MemberRole.USER)
                .build());
        return itemRepository.save(Item.builder()
                .member(owner)
                .category(Category.TOOL)
                .itemName("전동드릴")
                .description("설명")
                .rentalPoint(1000)
                .rentalPlaceName("우리집 앞")
                .rentalPlace("서울시 강남구")
                .rentalPlaceDetail("1층")
                .latitude(37.5)
                .longitude(127.0)
                .build());
    }

    private ItemImage createImage(Item item, String originalFileName, boolean mainImage) {
        return ItemImage.builder()
                .item(item)
                .originalFileName(originalFileName)
                .storedFileName("stored-" + originalFileName)
                .imagePath("/path/" + originalFileName)
                .mainImage(mainImage)
                .build();
    }
}
