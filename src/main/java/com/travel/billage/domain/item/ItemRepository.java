package com.travel.billage.domain.item;

import com.travel.billage.domain.category.Category;
import com.travel.billage.domain.member.Member;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByOrderByItemNoDesc();

    List<Item> findByCategoryOrderByItemNoDesc(Category category);

    List<Item> findByMember(Member member);

    List<Item> findByItemNameContainingOrderByItemNoDesc(String keyword);

    List<Item> findByCategoryAndItemNameContainingOrderByItemNoDesc(Category category, String keyword);
}
