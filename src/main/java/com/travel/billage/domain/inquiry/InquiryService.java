package com.travel.billage.domain.inquiry;

import com.travel.billage.domain.member.Member;
import com.travel.billage.domain.member.MemberRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InquiryService {

    private final InquiryRepository inquiryRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Inquiry createInquiry(Long memberNo, InquiryType inquiryType, String inquiryContent) {
        Member member = memberRepository.findById(memberNo)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        Inquiry inquiry = Inquiry.builder()
                .member(member)
                .inquiryType(inquiryType)
                .inquiryContent(inquiryContent)
                .build();
        return inquiryRepository.save(inquiry);
    }

    public List<Inquiry> getMyInquiries(Long memberNo) {
        Member member = memberRepository.findById(memberNo)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        return inquiryRepository.findByMember(member);
    }

    public List<Inquiry> getInquiriesByStatus(InquiryStatus processStatus) {
        return inquiryRepository.findByProcessStatus(processStatus);
    }

    @Transactional
    public void changeProcessStatus(Long inquiryNo, InquiryStatus processStatus, String adminComment) {
        Inquiry inquiry = inquiryRepository.findById(inquiryNo)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 문의입니다."));
        inquiry.changeProcessStatus(processStatus, adminComment);
    }
}