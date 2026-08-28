package com.sping.billage.domain.inquiry.repository;

import com.sping.billage.config.JpaAuditingConfig;
import com.sping.billage.domain.inquiry.entity.Inquiry;
import com.sping.billage.domain.inquiry.enums.InquiryStatus;
import com.sping.billage.domain.inquiry.enums.InquiryType;
import com.sping.billage.domain.member.entity.Member;
import com.sping.billage.domain.member.enums.MemberRole;
import com.sping.billage.domain.member.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;

import java.util.UUID;

@Slf4j
@DataJpaTest
@Import(JpaAuditingConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class InquiryRepositoryTest {

    @Autowired
    private InquiryRepository inquiryRepository;
    @Autowired
    private MemberRepository memberRepository;

    @Test
    void testSaveAndRead() {
        Member member = memberRepository.saveAndFlush(member("user"));

        Inquiry inquiry = inquiryRepository.saveAndFlush(Inquiry.builder()
                .type(InquiryType.RENTAL)
                .content("대여 승인은 언제 되나요?")
                .member(member)
                .build());

        log.info("===== 문의 정보 =====");
        log.info("id: {}", inquiry.getId());
        log.info("type: {}", inquiry.getType());
        log.info("content: {}", inquiry.getContent());
        log.info("status: {}", inquiry.getStatus());
        log.info("createdAt: {}", inquiry.getCreatedAt());

        inquiryRepository.findByMemberIdOrderByIdDesc(member.getId(), PageRequest.of(0, 10))
                .forEach(row -> log.info("내 문의: id={}, status={}", row.getId(), row.getStatus()));

        inquiry.answer("승인 후 바로 안내드립니다.");
        inquiryRepository.saveAndFlush(inquiry);

        Inquiry answered = inquiryRepository.findById(inquiry.getId())
                .orElseThrow(() -> new IllegalArgumentException("문의가 존재하지 않습니다."));
        log.info("답변 후 status: {}", answered.getStatus());
        log.info("answer: {}", answered.getAnswer());

        inquiryRepository.findAllForAdmin(InquiryStatus.ANSWERED, PageRequest.of(0, 10))
                .forEach(row -> log.info("관리자 조회(ANSWERED): id={}, answer={}",
                        row.getId(), row.getAnswer()));
    }

    private Member member(String prefix) {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        return Member.builder()
                .email(prefix + "-" + suffix + "@test.com")
                .password("encoded")
                .nickname(prefix + "-" + suffix)
                .role(MemberRole.USER)
                .build();
    }
}
