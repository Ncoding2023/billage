package com.travel.billage.domain.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.travel.billage.domain.item.Item;
import com.travel.billage.domain.item.ItemRepository;
import com.travel.billage.domain.point.PointHistory;
import com.travel.billage.domain.point.PointHistoryRepository;
import com.travel.billage.domain.point.PointService;
import com.travel.billage.domain.rental.Rental;
import com.travel.billage.domain.rental.RentalRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private RentalRepository rentalRepository;
    @Mock
    private PointHistoryRepository pointHistoryRepository;
    @Mock
    private PointService pointService;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private MemberService memberService;

    @Test
    void signUp_success() {
        when(memberRepository.existsByEmail("test@billage.com")).thenReturn(false);
        when(memberRepository.existsByNickname("tester")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded-password");
        when(memberRepository.save(any(Member.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Member member = memberService.signUp(
                "test@billage.com", "password123", "홍길동", "tester", "010-1111-2222");

        assertThat(member.getEmail()).isEqualTo("test@billage.com");
        assertThat(member.getPassword()).isEqualTo("encoded-password");
        assertThat(member.getRole()).isEqualTo(MemberRole.USER);
        verify(pointService).grantSignupBonus(member);
    }

    @Test
    void signUp_duplicateEmail_throws() {
        when(memberRepository.existsByEmail("dup@billage.com")).thenReturn(true);

        assertThatThrownBy(() -> memberService.signUp(
                "dup@billage.com", "password123", "홍길동", "tester", "010-1111-2222"))
                .isInstanceOf(IllegalArgumentException.class);

        verify(memberRepository, never()).save(any());
        verify(pointService, never()).grantSignupBonus(any());
    }

    @Test
    void signUp_duplicateNickname_throws() {
        when(memberRepository.existsByEmail("test@billage.com")).thenReturn(false);
        when(memberRepository.existsByNickname("tester")).thenReturn(true);

        assertThatThrownBy(() -> memberService.signUp(
                "test@billage.com", "password123", "홍길동", "tester", "010-1111-2222"))
                .isInstanceOf(IllegalArgumentException.class);

        verify(memberRepository, never()).save(any());
    }

    @Test
    void getMember_notFound_throws() {
        when(memberRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> memberService.getMember(999L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void updateProfile_updatesFields() {
        Member member = createMember(1L);
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        memberService.updateProfile(1L, "새이름", "새닉네임", "010-9999-8888");

        assertThat(member.getName()).isEqualTo("새이름");
        assertThat(member.getNickname()).isEqualTo("새닉네임");
        assertThat(member.getPhone()).isEqualTo("010-9999-8888");
    }

    @Test
    void getPointBalance_delegatesToPointService() {
        Member member = createMember(1L);
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(pointService.getBalance(member)).thenReturn(5000);

        assertThat(memberService.getPointBalance(1L)).isEqualTo(5000);
    }

    @Test
    void getPointHistories_delegatesToRepository() {
        Member member = createMember(1L);
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        List<PointHistory> histories = List.of();
        when(pointHistoryRepository.findByMemberOrderByChangedAtDesc(member)).thenReturn(histories);

        assertThat(memberService.getPointHistories(1L)).isSameAs(histories);
    }

    @Test
    void getMyItems_delegatesToRepository() {
        Member member = createMember(1L);
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        List<Item> items = List.of();
        when(itemRepository.findByMember(member)).thenReturn(items);

        assertThat(memberService.getMyItems(1L)).isSameAs(items);
    }

    @Test
    void getMyRentals_delegatesToRepository() {
        Member member = createMember(1L);
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        List<Rental> rentals = List.of();
        when(rentalRepository.findByMember(member)).thenReturn(rentals);

        assertThat(memberService.getMyRentals(1L)).isSameAs(rentals);
    }

    private Member createMember(Long memberNo) {
        Member member = Member.builder()
                .email("test@billage.com")
                .password("encoded")
                .name("홍길동")
                .nickname("tester")
                .phone("010-1111-2222")
                .role(MemberRole.USER)
                .build();
        ReflectionTestUtils.setField(member, "memberNo", memberNo);
        return member;
    }
}
