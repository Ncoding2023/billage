package com.travel.billage.domain.item;

import static org.assertj.core.api.Assertions.assertThat;

import com.travel.billage.domain.category.Category;
import com.travel.billage.domain.member.Member;
import com.travel.billage.domain.member.MemberRepository;
import com.travel.billage.domain.member.MemberRole;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private MemberRepository memberRepository;

    @Test
    void findByCategory_returnsOnlyMatchingCategory() {
        Member owner = memberRepository.save(createMember("owner1"));
        itemRepository.save(createItem(owner, Category.TOOL, "전동드릴"));
        itemRepository.save(createItem(owner, Category.CLOTHES, "정장"));

        List<Item> tools = itemRepository.findByCategory(Category.TOOL);

        assertThat(tools).extracting(Item::getItemName).containsExactly("전동드릴");
    }

    @Test
    void findByMember_returnsOnlyOwnedItems() {
        Member owner1 = memberRepository.save(createMember("owner1"));
        Member owner2 = memberRepository.save(createMember("owner2"));
        itemRepository.save(createItem(owner1, Category.TOOL, "전동드릴"));
        itemRepository.save(createItem(owner2, Category.TOOL, "망치"));

        List<Item> items = itemRepository.findByMember(owner1);

        assertThat(items).extracting(Item::getItemName).containsExactly("전동드릴");
    }

    @Test
    void findByItemNameContaining_matchesSubstring() {
        Member owner = memberRepository.save(createMember("owner1"));
        itemRepository.save(createItem(owner, Category.TOOL, "보쉬 전동드릴"));
        itemRepository.save(createItem(owner, Category.CLOTHES, "정장 자켓"));

        List<Item> items = itemRepository.findByItemNameContaining("드릴");

        assertThat(items).extracting(Item::getItemName).containsExactly("보쉬 전동드릴");
    }

    @Test
    void findByCategoryAndItemNameContaining_combinesFilters() {
        Member owner = memberRepository.save(createMember("owner1"));
        itemRepository.save(createItem(owner, Category.TOOL, "보쉬 드릴"));
        itemRepository.save(createItem(owner, Category.CAMPING, "캠핑용 드릴"));

        List<Item> items = itemRepository.findByCategoryAndItemNameContaining(Category.TOOL, "드릴");

        assertThat(items).extracting(Item::getItemName).containsExactly("보쉬 드릴");
    }

    private Member createMember(String nickname) {
        return Member.builder()
                .email(nickname + "@billage.com")
                .password("encoded")
                .name("소유자")
                .nickname(nickname)
                .phone("010-1111-2222")
                .role(MemberRole.USER)
                .build();
    }

    private Item createItem(Member owner, Category category, String itemName) {
        return Item.builder()
                .member(owner)
                .category(category)
                .itemName(itemName)
                .description("설명")
                .rentalPoint(1000)
                .rentalPlaceName("우리집 앞")
                .rentalPlace("서울시 강남구")
                .rentalPlaceDetail("1층")
                .latitude(37.5)
                .longitude(127.0)
                .build();
    }
}
