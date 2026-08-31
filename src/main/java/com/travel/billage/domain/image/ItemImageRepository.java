package com.travel.billage.domain.image;

import com.travel.billage.domain.item.Item;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemImageRepository extends JpaRepository<ItemImage, Long> {

    List<ItemImage> findByItem(Item item);

    Optional<ItemImage> findByItemAndMainImageTrue(Item item);
}
