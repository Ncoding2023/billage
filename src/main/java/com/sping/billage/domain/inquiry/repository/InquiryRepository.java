package com.sping.billage.domain.inquiry.repository;

import com.sping.billage.domain.inquiry.entity.Inquiry;
import com.sping.billage.domain.inquiry.enums.InquiryStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {

    Page<Inquiry> findByMemberIdOrderByIdDesc(Long memberId, Pageable pageable);

    @Query(value = """
            select i from Inquiry i
            join fetch i.member
            where (:status is null or i.status = :status)
            order by i.id desc
            """,
            countQuery = """
                    select count(i) from Inquiry i
                    where (:status is null or i.status = :status)
                    """)
    Page<Inquiry> findAllForAdmin(@Param("status") InquiryStatus status, Pageable pageable);
}
