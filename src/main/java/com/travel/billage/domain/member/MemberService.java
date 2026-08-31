package com.travel.billage.domain.member;

import com.travel.billage.domain.item.Item;
import com.travel.billage.domain.item.ItemRepository;
import com.travel.billage.domain.point.PointHistory;
import com.travel.billage.domain.point.PointHistoryRepository;
import com.travel.billage.domain.point.PointService;
import com.travel.billage.domain.rental.Rental;
import com.travel.billage.domain.rental.RentalRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;
    private final RentalRepository rentalRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final PointService pointService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Member signUp(String email, String password, String name, String nickname, String phone) {
        if (memberRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
        if (memberRepository.existsByNickname(nickname)) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        Member member = Member.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .name(name)
                .nickname(nickname)
                .phone(phone)
                .role(MemberRole.USER)
                .build();
        memberRepository.save(member);
        pointService.grantSignupBonus(member);
        return member;
    }

    public Member getMember(Long memberNo) {
        return memberRepository.findById(memberNo)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
    }

    @Transactional
    public void updateProfile(Long memberNo, String name, String nickname, String phone) {
        getMember(memberNo).updateProfile(name, nickname, phone);
    }

    public int getPointBalance(Long memberNo) {
        return pointService.getBalance(getMember(memberNo));
    }

    public List<PointHistory> getPointHistories(Long memberNo) {
        return pointHistoryRepository.findByMemberOrderByChangedAtDesc(getMember(memberNo));
    }

    public List<Item> getMyItems(Long memberNo) {
        return itemRepository.findByMember(getMember(memberNo));
    }

    public List<Rental> getMyRentals(Long memberNo) {
        return rentalRepository.findByMember(getMember(memberNo));
    }

    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    @Transactional
    public void suspendMember(Long memberNo) {
        getMember(memberNo).suspend();
    }

    @Transactional
    public void activateMember(Long memberNo) {
        getMember(memberNo).activate();
    }
}
