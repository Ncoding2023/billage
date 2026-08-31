package com.travel.billage.domain.inquiry;

import static org.assertj.core.api.Assertions.assertThat;

import com.travel.billage.domain.member.Member;
import com.travel.billage.domain.member.MemberRepository;
import com.travel.billage.domain.member.MemberRole;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

@DataJpaTest
class InquiryRepositoryTest {

    @Autowired
    private InquiryRepository inquiryRepository;
    @Autowired
    private MemberRepository memberRepository;

    @Test
    void findByMember_returnsOnlyThatMembersInquiries() {
        Member member1 = saveMember("member1");
        Member member2 = saveMember("member2");
        inquiryRepository.save(createInquiry(member1, InquiryType.INQUIRY, "문의1"));
        inquiryRepository.save(createInquiry(member2, InquiryType.REPORT, "신고1"));

        List<Inquiry> inquiries = inquiryRepository.findByMember(member1);

        assertThat(inquiries).extracting(Inquiry::getInquiryContent).containsExactly("문의1");
    }

    @Test
    void findByProcessStatus_filtersByStatus() {
        Member member = saveMember("member1");
        Inquiry received = inquiryRepository.save(createInquiry(member, InquiryType.INQUIRY, "접수건"));
        Inquiry completed = inquiryRepository.save(createInquiry(member, InquiryType.INQUIRY, "완료건"));
        completed.changeProcessStatus(InquiryStatus.COMPLETED);
        inquiryRepository.save(completed);

        List<Inquiry> receivedList = inquiryRepository.findByProcessStatus(InquiryStatus.RECEIVED);
        List<Inquiry> completedList = inquiryRepository.findByProcessStatus(InquiryStatus.COMPLETED);

        assertThat(receivedList).extracting(Inquiry::getInquiryContent).containsExactly("접수건");
        assertThat(completedList).extracting(Inquiry::getInquiryContent).containsExactly("완료건");
    }

    private Member saveMember(String nickname) {
        return memberRepository.save(Member.builder()
                .email(nickname + "@billage.com")
                .password("encoded")
                .name(nickname)
                .nickname(nickname)
                .phone("010-1111-2222")
                .role(MemberRole.USER)
                .build());
    }

    private Inquiry createInquiry(Member member, InquiryType type, String content) {
        return Inquiry.builder()
                .member(member)
                .inquiryType(type)
                .inquiryContent(content)
                .build();
    }
}
