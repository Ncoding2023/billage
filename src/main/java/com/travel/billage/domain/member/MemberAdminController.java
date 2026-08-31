package com.travel.billage.domain.member;

import com.travel.billage.domain.member.dto.MemberResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/members")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class MemberAdminController {

    private final MemberService memberService;

    @GetMapping
    public List<MemberResponse> getAllMembers() {
        return memberService.getAllMembers().stream()
                .map(MemberResponse::from)
                .toList();
    }

    @GetMapping("/{memberNo}")
    public MemberResponse getMember(@PathVariable Long memberNo) {
        return MemberResponse.from(memberService.getMember(memberNo));
    }

    @PatchMapping("/{memberNo}/suspend")
    public MemberResponse suspendMember(@PathVariable Long memberNo) {
        memberService.suspendMember(memberNo);
        return MemberResponse.from(memberService.getMember(memberNo));
    }

    @PatchMapping("/{memberNo}/activate")
    public MemberResponse activateMember(@PathVariable Long memberNo) {
        memberService.activateMember(memberNo);
        return MemberResponse.from(memberService.getMember(memberNo));
    }
}
