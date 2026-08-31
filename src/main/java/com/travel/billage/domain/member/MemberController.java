package com.travel.billage.domain.member;

import com.travel.billage.domain.item.dto.ItemResponse;
import com.travel.billage.domain.member.dto.MemberResponse;
import com.travel.billage.domain.member.dto.PointBalanceResponse;
import com.travel.billage.domain.member.dto.PointHistoryResponse;
import com.travel.billage.domain.member.dto.SignUpRequest;
import com.travel.billage.domain.member.dto.UpdateProfileRequest;
import com.travel.billage.domain.rental.dto.RentalResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private static final String SELF_OR_ADMIN =
            "#memberNo == authentication.principal.memberNo or hasRole('ADMIN')";

    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity<MemberResponse> signUp(@Valid @RequestBody SignUpRequest request) {
        Member member = memberService.signUp(
                request.email(), request.password(), request.name(), request.nickname(), request.phone());
        return ResponseEntity.status(HttpStatus.CREATED).body(MemberResponse.from(member));
    }

    @PreAuthorize(SELF_OR_ADMIN)
    @GetMapping("/{memberNo}")
    public MemberResponse getMember(@PathVariable Long memberNo) {
        return MemberResponse.from(memberService.getMember(memberNo));
    }

    @PreAuthorize(SELF_OR_ADMIN)
    @PatchMapping("/{memberNo}")
    public MemberResponse updateProfile(@PathVariable Long memberNo, @Valid @RequestBody UpdateProfileRequest request) {
        memberService.updateProfile(memberNo, request.name(), request.nickname(), request.phone());
        return MemberResponse.from(memberService.getMember(memberNo));
    }

    @PreAuthorize(SELF_OR_ADMIN)
    @GetMapping("/{memberNo}/points/balance")
    public PointBalanceResponse getPointBalance(@PathVariable Long memberNo) {
        return new PointBalanceResponse(memberService.getPointBalance(memberNo));
    }

    @PreAuthorize(SELF_OR_ADMIN)
    @GetMapping("/{memberNo}/points/histories")
    public List<PointHistoryResponse> getPointHistories(@PathVariable Long memberNo) {
        return memberService.getPointHistories(memberNo).stream()
                .map(PointHistoryResponse::from)
                .toList();
    }

    @PreAuthorize(SELF_OR_ADMIN)
    @GetMapping("/{memberNo}/items")
    public List<ItemResponse> getMyItems(@PathVariable Long memberNo) {
        return memberService.getMyItems(memberNo).stream()
                .map(ItemResponse::from)
                .toList();
    }

    @PreAuthorize(SELF_OR_ADMIN)
    @GetMapping("/{memberNo}/rentals")
    public List<RentalResponse> getMyRentals(@PathVariable Long memberNo) {
        return memberService.getMyRentals(memberNo).stream()
                .map(RentalResponse::from)
                .toList();
    }
}
