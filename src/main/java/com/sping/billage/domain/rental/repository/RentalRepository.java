package com.sping.billage.domain.rental.repository;

import com.sping.billage.domain.rental.entity.Rental;
import com.sping.billage.domain.rental.enums.RentalStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface RentalRepository extends JpaRepository<Rental, Long> {

    @Query("select r from Rental r join fetch r.item i join fetch i.owner where r.id = :id")
    Optional<Rental> findWithItemById(@Param("id") Long id);

    @Query(value = """
            select r from Rental r
            join fetch r.item i
            join fetch i.owner
            where r.renter.id = :memberId
            """,
            countQuery = "select count(r) from Rental r where r.renter.id = :memberId")
    Page<Rental> findByRenterId(@Param("memberId") Long memberId, Pageable pageable);

    @Query("""
            select r from Rental r
            join fetch r.renter
            where r.item.id = :itemId
            order by r.requestedAt desc
            """)
    List<Rental> findByItemIdWithRenter(@Param("itemId") Long itemId);

    /**
     * 같은 물품에 대해 기간이 겹치는 대여가 있는지 확인한다.
     */
    @Query("""
            select count(r) > 0 from Rental r
            where r.item.id = :itemId
              and r.status in :statuses
              and r.startDate <= :endDate
              and r.endDate >= :startDate
            """)
    boolean existsOverlappedRental(@Param("itemId") Long itemId,
                                   @Param("startDate") LocalDate startDate,
                                   @Param("endDate") LocalDate endDate,
                                   @Param("statuses") Collection<RentalStatus> statuses);

    boolean existsByItemIdAndStatusIn(Long itemId, Collection<RentalStatus> statuses);

    boolean existsByItemId(Long itemId);
}
