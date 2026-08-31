package com.travel.billage.domain.inquiry;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.travel.billage.domain.member.Member;
import com.travel.billage.domain.member.MemberRepository;
import com.travel.billage.domain.member.MemberRole;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InquiryServiceTest {

    @Mock
    private InquiryRepository inquiryRepository;
    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private InquiryService inquiryService;

    @Test
    void createInquiry_success() {
        Member member = createMember();
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(inquiryRepository.save(any(Inquiry.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Inquiry inquiry = inquiryService.createInquiry(1L, InquiryType.REPORT, "노쇼 신고합니다");

        assertThat(inquiry.getMember()).isEqualTo(member);
        assertThat(inquiry.getInquiryType()).isEqualTo(InquiryType.REPORT);
        assertThat(inquiry.getInquiryContent()).isEqualTo("노쇼 신고합니다");
        assertThat(inquiry.getProcessStatus()).isEqualTo(InquiryStatus.RECEIVED);
    }

    @Test
    void createInquiry_memberNotFound_throws() {
        when(memberRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> inquiryService.createInquiry(999L, InquiryType.INQUIRY, "문의합니다"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getMyInquiries_delegatesToRepository() {
        Member member = createMember();
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        List<Inquiry> inquiries = List.of();
        when(inquiryRepository.findByMember(member)).thenReturn(inquiries);

        assertThat(inquiryService.getMyInquiries(1L)).isSameAs(inquiries);
    }

    @Test
    void getInquiriesByStatus_delegatesToRepository() {
        List<Inquiry> inquiries = List.of();
        when(inquiryRepository.findByProcessStatus(InquiryStatus.RECEIVED)).thenReturn(inquiries);

        assertThat(inquiryService.getInquiriesByStatus(InquiryStatus.RECEIVED)).isSameAs(inquiries);
    }

    @Test
    void changeProcessStatus_updatesStatus() {
        Member member = createMember();
        Inquiry inquiry = Inquiry.builder()
                .member(member)
                .inquiryType(InquiryType.INQUIRY)
                .inquiryContent("문의합니다")
                .build();
        when(inquiryRepository.findById(1L)).thenReturn(Optional.of(inquiry));

        inquiryService.changeProcessStatus(1L, InquiryStatus.COMPLETED);

        assertThat(inquiry.getProcessStatus()).isEqualTo(InquiryStatus.COMPLETED);
    }

    @Test
    void changeProcessStatus_notFound_throws() {
        when(inquiryRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> inquiryService.changeProcessStatus(999L, InquiryStatus.COMPLETED))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private Member createMember() {
        return Member.builder()
                .email("member@billage.com")
                .password("encoded")
                .name("회원")
                .nickname("member")
                .phone("010-1111-2222")
                .role(MemberRole.USER)
                .build();
    }
}
