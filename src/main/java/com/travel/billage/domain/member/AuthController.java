package com.travel.billage.domain.member;

import com.travel.billage.domain.member.dto.LoginRequest;
import com.travel.billage.domain.member.dto.LoginResponse;
import com.travel.billage.domain.member.dto.MemberResponse;
import com.travel.billage.domain.member.dto.ResetPasswordRequest;
import com.travel.billage.security.MemberDetails;
import com.travel.billage.security.jwt.JwtTokenProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberService memberService;

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        MemberDetails memberDetails = (MemberDetails) authentication.getPrincipal();
        String accessToken = jwtTokenProvider.generateToken(memberDetails);
        Member member = memberService.getMember(memberDetails.getMemberNo());

        return LoginResponse.of(accessToken, MemberResponse.from(member));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        memberService.resetPassword(
                request.email(), request.name(), request.newPassword(), request.confirmPassword());
        return ResponseEntity.noContent().build();
    }
}
