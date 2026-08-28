package com.sping.billage.domain.item.repository;

import com.sping.billage.domain.item.entity.Item;
import com.sping.billage.domain.item.enums.ItemCategory;
import com.sping.billage.domain.item.enums.ItemStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query(value = """
            select i from Item i
            join fetch i.owner
            left join fetch i.place
            where (:category is null or i.category = :category)
              and (:status is null or i.status = :status)
              and (:keyword is null or lower(i.name) like lower(concat('%', :keyword, '%')))
            """,
            countQuery = """
                    select count(i) from Item i
                    where (:category is null or i.category = :category)
                      and (:status is null or i.status = :status)
                      and (:keyword is null or lower(i.name) like lower(concat('%', :keyword, '%')))
                    """)
    Page<Item> search(@Param("category") ItemCategory category,
                      @Param("status") ItemStatus status,
                      @Param("keyword") String keyword,
                      Pageable pageable);

    @Query("select i from Item i join fetch i.owner left join fetch i.place where i.id = :id")
    Optional<Item> findWithOwnerAndPlaceById(@Param("id") Long id);

    Page<Item> findByOwnerId(Long ownerId, Pageable pageable);
}
