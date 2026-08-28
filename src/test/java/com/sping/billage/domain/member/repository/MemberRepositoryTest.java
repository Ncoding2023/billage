package com.sping.billage.domain.member.repository;

import com.sping.billage.config.JpaAuditingConfig;
import com.sping.billage.domain.member.entity.Member;
import com.sping.billage.domain.member.enums.MemberRole;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import java.util.UUID;

@Slf4j
@DataJpaTest
@Import(JpaAuditingConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void testSaveAndRead() {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        String email = "user-" + suffix + "@test.com";

        Member saved = memberRepository.saveAndFlush(Member.builder()
                .email(email)
                .password("encoded-password")
                .nickname("???-" + suffix)
                .role(MemberRole.USER)
                .build());

        log.info("??? ?? ID: {}", saved.getId());

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("??? ???? ????."));

        printMember(member);
        log.info("??: {}", member.getRole());
        log.info("??? ?? ??: {}", memberRepository.existsByEmail(email));
        log.info("??? ?? ??: {}", memberRepository.existsByNickname(member.getNickname()));
    }

    private void printMember(Member member) {
        log.info("===== ?? ?? =====");
        log.info("id: {}", member.getId());
        log.info("email: {}", member.getEmail());
        log.info("nickname: {}", member.getNickname());
        log.info("role: {}", member.getRole());
        log.info("createdAt: {}", member.getCreatedAt());
        log.info("updatedAt: {}", member.getUpdatedAt());
    }
}
