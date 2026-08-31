package com.travel.billage.domain.point;

import com.travel.billage.domain.member.Member;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {

    List<PointHistory> findByMemberOrderByChangedAtDesc(Member member);

    @Query("select coalesce(sum(p.pointAmount), 0) from PointHistory p where p.member = :member")
    int sumPointAmountByMember(@Param("member") Member member);
}
