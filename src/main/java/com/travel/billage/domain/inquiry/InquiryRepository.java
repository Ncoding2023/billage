package com.travel.billage.domain.inquiry;

import com.travel.billage.domain.member.Member;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {

    List<Inquiry> findByMember(Member member);

    List<Inquiry> findByProcessStatus(InquiryStatus processStatus);
}
