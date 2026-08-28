package com.sping.billage.domain.point.repository;

import com.sping.billage.domain.point.entity.PointHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {

    /**
     * 포인트 잔액 = 이력 합계. Member에 잔액 컬럼을 두지 않는다.
     */
    @Query("select coalesce(sum(p.amount), 0) from PointHistory p where p.member.id = :memberId")
    long sumAmountByMemberId(@Param("memberId") Long memberId);

    Page<PointHistory> findByMemberIdOrderByIdDesc(Long memberId, Pageable pageable);

    List<PointHistory> findByRentalIdOrderByIdAsc(Long rentalId);
}
