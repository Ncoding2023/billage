package com.travel.billage.domain.item;

import com.travel.billage.domain.category.Category;
import com.travel.billage.domain.member.Member;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByCategory(Category category);

    List<Item> findByMember(Member member);

    List<Item> findByItemNameContaining(String keyword);

    List<Item> findByCategoryAndItemNameContaining(Category category, String keyword);
}
