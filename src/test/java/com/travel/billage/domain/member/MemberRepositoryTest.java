package com.travel.billage.domain.member;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

@DataJpaTest
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void findByEmail_found() {
        memberRepository.save(createMember("a@billage.com", "nick1"));

        Optional<Member> found = memberRepository.findByEmail("a@billage.com");

        assertThat(found).isPresent();
        assertThat(found.get().getNickname()).isEqualTo("nick1");
    }

    @Test
    void findByEmail_notFound_returnsEmpty() {
        assertThat(memberRepository.findByEmail("nobody@billage.com")).isEmpty();
    }

    @Test
    void existsByEmail_reflectsSavedState() {
        memberRepository.save(createMember("a@billage.com", "nick1"));

        assertThat(memberRepository.existsByEmail("a@billage.com")).isTrue();
        assertThat(memberRepository.existsByEmail("b@billage.com")).isFalse();
    }

    @Test
    void existsByNickname_reflectsSavedState() {
        memberRepository.save(createMember("a@billage.com", "nick1"));

        assertThat(memberRepository.existsByNickname("nick1")).isTrue();
        assertThat(memberRepository.existsByNickname("nick2")).isFalse();
    }

    private Member createMember(String email, String nickname) {
        return Member.builder()
                .email(email)
                .password("encoded")
                .name("홍길동")
                .nickname(nickname)
                .phone("010-1111-2222")
                .role(MemberRole.USER)
                .build();
    }
}
