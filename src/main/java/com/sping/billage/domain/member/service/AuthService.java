package com.sping.billage.domain.member.service;

import com.sping.billage.domain.member.dto.LoginRequest;
import com.sping.billage.domain.member.dto.LoginResponse;
import com.sping.billage.domain.member.dto.MemberResponse;
import com.sping.billage.domain.member.dto.SignupRequest;
import com.sping.billage.domain.member.entity.Member;
import com.sping.billage.domain.member.enums.MemberRole;
import com.sping.billage.domain.member.mapper.MemberMapper;
import com.sping.billage.domain.member.repository.MemberRepository;
import com.sping.billage.domain.point.service.PointService;
import com.sping.billage.global.constant.PointPolicy;
import com.sping.billage.global.exception.BusinessException;
import com.sping.billage.global.exception.ErrorCode;
import com.sping.billage.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final MemberRepository memberRepository;
    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final PointService pointService;

    /**
     * 회원가입. 비밀번호를 암호화하고 가입 보너스 포인트를 적립한다.
     */
    @Transactional
    public MemberResponse signup(SignupRequest request) {
        if (memberRepository.existsByEmail(request.email())) {
            throw new BusinessException(ErrorCode.EMAIL_DUPLICATED);
        }
        if (memberRepository.existsByNickname(request.nickname())) {
            throw new BusinessException(ErrorCode.NICKNAME_DUPLICATED);
        }

        Member member = memberMapper.toEntity(
                request, passwordEncoder.encode(request.password()), MemberRole.USER);
        memberRepository.save(member);

        pointService.earn(member, PointPolicy.SIGNUP_BONUS, PointPolicy.DESC_SIGNUP_BONUS, null);

        return memberMapper.toResponse(member, pointService.getBalance(member.getId()));
    }

    public LoginResponse login(LoginRequest request) {
        Member member = memberRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(request.password(), member.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }

        String accessToken = jwtTokenProvider.createAccessToken(
                member.getId(), member.getEmail(), member.getNickname(), member.getRole());

        return LoginResponse.of(
                accessToken,
                jwtTokenProvider.getAccessTokenValiditySeconds(),
                member.getId(),
                member.getNickname(),
                member.getRole());
    }
}
