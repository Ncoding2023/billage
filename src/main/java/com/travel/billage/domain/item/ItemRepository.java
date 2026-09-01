package com.travel.billage.domain.item;

import com.travel.billage.domain.category.Category;
import com.travel.billage.domain.member.Member;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Page<Item> findByCategory(Category category, Pageable pageable);

    Page<Item> findByItemNameContaining(String keyword, Pageable pageable);

    Page<Item> findByCategoryAndItemNameContaining(Category category, String keyword, Pageable pageable);

    List<Item> findByMember(Member member);
}
