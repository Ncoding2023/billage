package com.travel.billage.domain.point;

import static org.assertj.core.api.Assertions.assertThat;

import com.travel.billage.domain.member.Member;
import com.travel.billage.domain.member.MemberRepository;
import com.travel.billage.domain.member.MemberRole;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.util.ReflectionTestUtils;

@DataJpaTest
class PointHistoryRepositoryTest {

    @Autowired
    private PointHistoryRepository pointHistoryRepository;
    @Autowired
    private MemberRepository memberRepository;

    @Test
    void findByMemberOrderByChangedAtDesc_returnsNewestFirst() {
        Member member = saveMember();
        save(member, 5000, PointType.SIGNUP_BONUS, "가입", LocalDateTime.now().minusDays(2));
        save(member, -1000, PointType.RENTAL_PAYMENT, "대여1", LocalDateTime.now().minusDays(1));
        save(member, -500, PointType.RENTAL_PAYMENT, "대여2", LocalDateTime.now());

        List<PointHistory> histories = pointHistoryRepository.findByMemberOrderByChangedAtDesc(member);

        assertThat(histories).extracting(PointHistory::getPointContent)
                .containsExactly("대여2", "대여1", "가입");
    }

    @Test
    void sumPointAmountByMember_sumsAllAmounts() {
        Member member = saveMember();
        save(member, 5000, PointType.SIGNUP_BONUS, "가입", LocalDateTime.now());
        save(member, -1000, PointType.RENTAL_PAYMENT, "대여", LocalDateTime.now());

        assertThat(pointHistoryRepository.sumPointAmountByMember(member)).isEqualTo(4000);
    }

    @Test
    void sumPointAmountByMember_returnsZeroWhenNoHistory() {
        Member member = saveMember();

        assertThat(pointHistoryRepository.sumPointAmountByMember(member)).isZero();
    }

    private Member saveMember() {
        return memberRepository.save(Member.builder()
                .email("member@billage.com")
                .password("encoded")
                .name("회원")
                .nickname("member")
                .phone("010-1111-2222")
                .role(MemberRole.USER)
                .build());
    }

    private void save(Member member, int amount, PointType type, String content, LocalDateTime changedAt) {
        PointHistory history = PointHistory.builder()
                .member(member)
                .pointAmount(amount)
                .pointType(type)
                .pointContent(content)
                .build();
        ReflectionTestUtils.setField(history, "changedAt", changedAt);
        pointHistoryRepository.save(history);
    }
}
