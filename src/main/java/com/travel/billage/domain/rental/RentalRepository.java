package com.travel.billage.domain.rental;

import com.travel.billage.domain.item.Item;
import com.travel.billage.domain.member.Member;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RentalRepository extends JpaRepository<Rental, Long> {

    List<Rental> findByMember(Member member);

    List<Rental> findByItem(Item item);

    boolean existsByItem(Item item);

    List<Rental> findByItem_Member(Member member);

    @Query("select case when count(r) > 0 then true else false end "
            + "from Rental r "
            + "where r.item = :item "
            + "and r.rentalStatus <> com.travel.billage.domain.rental.RentalStatus.CANCELED "
            + "and r.rentalStartDate <= :endDate "
            + "and r.rentalEndDate >= :startDate")
    boolean existsOverlappingRental(@Param("item") Item item,
                                     @Param("startDate") LocalDate startDate,
                                     @Param("endDate") LocalDate endDate);
}
