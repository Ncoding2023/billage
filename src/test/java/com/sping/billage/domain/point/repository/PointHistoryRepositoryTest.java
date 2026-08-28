package com.sping.billage.domain.point.repository;

import com.sping.billage.config.JpaAuditingConfig;
import com.sping.billage.domain.member.entity.Member;
import com.sping.billage.domain.member.enums.MemberRole;
import com.sping.billage.domain.member.repository.MemberRepository;
import com.sping.billage.domain.point.entity.PointHistory;
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
class PointHistoryRepositoryTest {

    @Autowired
    private PointHistoryRepository pointHistoryRepository;
    @Autowired
    private MemberRepository memberRepository;

    @Test
    void testSaveAndRead() {
        Member member = memberRepository.saveAndFlush(member("point"));

        pointHistoryRepository.saveAndFlush(PointHistory.builder()
                .amount(1000L)
                .description("???? ???")
                .member(member)
                .build());
        pointHistoryRepository.saveAndFlush(PointHistory.builder()
                .amount(-300L)
                .description("?? ??")
                .member(member)
                .build());

        long balance = pointHistoryRepository.sumAmountByMemberId(member.getId());
        log.info("===== ??? ?? =====");
        log.info("memberId: {}", member.getId());
        log.info("??(?? ??): {}", balance);

        pointHistoryRepository.findByMemberIdOrderByIdDesc(member.getId(),
                        org.springframework.data.domain.PageRequest.of(0, 10))
                .forEach(history -> log.info("??: id={}, amount={}, description={}",
                        history.getId(), history.getAmount(), history.getDescription()));
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
