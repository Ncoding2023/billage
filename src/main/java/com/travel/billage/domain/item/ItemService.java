package com.travel.billage.domain.item;

import com.travel.billage.domain.category.Category;
import com.travel.billage.domain.member.Member;
import com.travel.billage.domain.member.MemberRepository;
import com.travel.billage.domain.rental.RentalRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemService {

    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final RentalRepository rentalRepository;

    @Transactional
    public Item registerItem(Long memberNo, Category category, String itemName, String description,
                              Integer rentalPoint, String rentalPlaceName, String rentalPlace,
                              String rentalPlaceDetail, Double latitude, Double longitude) {
        Member member = memberRepository.findById(memberNo)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        Item item = Item.builder()
                .member(member)
                .category(category)
                .itemName(itemName)
                .description(description)
                .rentalPoint(rentalPoint)
                .rentalPlaceName(rentalPlaceName)
                .rentalPlace(rentalPlace)
                .rentalPlaceDetail(rentalPlaceDetail)
                .latitude(latitude)
                .longitude(longitude)
                .build();
        return itemRepository.save(item);
    }

    public Item getItem(Long itemNo) {
        return itemRepository.findById(itemNo)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 물품입니다."));
    }

    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    public List<Item> getItemsByCategory(Category category) {
        return itemRepository.findByCategory(category);
    }

    public List<Item> searchItems(String keyword) {
        return itemRepository.findByItemNameContaining(keyword);
    }

    public List<Item> searchItemsByCategory(Category category, String keyword) {
        return itemRepository.findByCategoryAndItemNameContaining(category, keyword);
    }

    @Transactional
    public void updateItem(Long itemNo, Long memberNo, Category category, String itemName, String description,
                            Integer rentalPoint, String rentalPlaceName, String rentalPlace,
                            String rentalPlaceDetail, Double latitude, Double longitude) {
        Item item = getItem(itemNo);
        validateOwner(item, memberNo);
        item.updateItem(category, itemName, description, rentalPoint, rentalPlaceName, rentalPlace,
                rentalPlaceDetail, latitude, longitude);
    }

    @Transactional
    public void changeStatus(Long itemNo, Long memberNo, ItemStatus itemStatus) {
        Item item = getItem(itemNo);
        validateOwner(item, memberNo);
        item.changeStatus(itemStatus);
    }

    @Transactional
    public void deleteItem(Long itemNo, Long memberNo) {
        Item item = getItem(itemNo);
        validateOwner(item, memberNo);
        deleteWithRentalCheck(item);
    }

    @Transactional
    public void adminChangeStatus(Long itemNo, ItemStatus itemStatus) {
        getItem(itemNo).changeStatus(itemStatus);
    }

    @Transactional
    public void adminDeleteItem(Long itemNo) {
        deleteWithRentalCheck(getItem(itemNo));
    }

    private void deleteWithRentalCheck(Item item) {
        if (rentalRepository.existsByItem(item)) {
            throw new IllegalStateException("대여 이력이 있는 물품은 삭제할 수 없습니다. 먼저 비활성화 처리해주세요.");
        }
        itemRepository.delete(item);
    }

    private void validateOwner(Item item, Long memberNo) {
        if (!item.getMember().getMemberNo().equals(memberNo)) {
            throw new IllegalStateException("물품에 대한 권한이 없습니다.");
        }
    }
}
