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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    private static final String EMAIL = "user@billage.com";
    private static final String RAW_PASSWORD = "billage1234";
    private static final String ENCODED_PASSWORD = "$2a$10$encoded";
    private static final String NICKNAME = "빌리지유저";

    @Mock
    private MemberRepository memberRepository;
    @Mock
    private MemberMapper memberMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private PointService pointService;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("회원가입 시 비밀번호가 암호화되고 가입 보너스 포인트가 적립된다")
    void signup_encodesPasswordAndEarnsSignupBonus() {
        SignupRequest request = new SignupRequest(EMAIL, RAW_PASSWORD, NICKNAME);
        Member member = member();

        given(memberRepository.existsByEmail(EMAIL)).willReturn(false);
        given(memberRepository.existsByNickname(NICKNAME)).willReturn(false);
        given(passwordEncoder.encode(RAW_PASSWORD)).willReturn(ENCODED_PASSWORD);
        given(memberMapper.toEntity(request, ENCODED_PASSWORD, MemberRole.USER)).willReturn(member);
        given(pointService.getBalance(any())).willReturn(PointPolicy.SIGNUP_BONUS);
        given(memberMapper.toResponse(eq(member), anyLong())).willReturn(
                new MemberResponse(1L, EMAIL, NICKNAME, MemberRole.USER,
                        PointPolicy.SIGNUP_BONUS, LocalDateTime.now()));

        MemberResponse response = authService.signup(request);

        verify(memberRepository).save(member);
        verify(pointService).earn(member, PointPolicy.SIGNUP_BONUS, PointPolicy.DESC_SIGNUP_BONUS, null);
        assertThat(member.getPassword()).isEqualTo(ENCODED_PASSWORD);
        assertThat(response.pointBalance()).isEqualTo(PointPolicy.SIGNUP_BONUS);
        assertThat(response.role()).isEqualTo(MemberRole.USER);
    }

    @Test
    @DisplayName("중복 이메일로 회원가입하면 예외가 발생하고 포인트도 적립되지 않는다")
    void signup_duplicatedEmail_throws() {
        SignupRequest request = new SignupRequest(EMAIL, RAW_PASSWORD, NICKNAME);
        given(memberRepository.existsByEmail(EMAIL)).willReturn(true);

        assertThatThrownBy(() -> authService.signup(request))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.EMAIL_DUPLICATED);

        verify(memberRepository, never()).save(any());
        verify(pointService, never()).earn(any(), anyLong(), any(), any());
    }

    @Test
    @DisplayName("중복 닉네임으로 회원가입하면 예외가 발생한다")
    void signup_duplicatedNickname_throws() {
        SignupRequest request = new SignupRequest(EMAIL, RAW_PASSWORD, NICKNAME);
        given(memberRepository.existsByEmail(EMAIL)).willReturn(false);
        given(memberRepository.existsByNickname(NICKNAME)).willReturn(true);

        assertThatThrownBy(() -> authService.signup(request))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NICKNAME_DUPLICATED);

        verify(memberRepository, never()).save(any());
    }

    @Test
    @DisplayName("로그인 성공 시 JWT 토큰과 닉네임, 권한을 반환한다")
    void login_success_returnsToken() {
        LoginRequest request = new LoginRequest(EMAIL, RAW_PASSWORD);
        Member member = member();

        given(memberRepository.findByEmail(EMAIL)).willReturn(Optional.of(member));
        given(passwordEncoder.matches(RAW_PASSWORD, ENCODED_PASSWORD)).willReturn(true);
        given(jwtTokenProvider.createAccessToken(any(), eq(EMAIL), eq(NICKNAME), eq(MemberRole.USER)))
                .willReturn("access.token.value");
        given(jwtTokenProvider.getAccessTokenValiditySeconds()).willReturn(86400L);

        LoginResponse response = authService.login(request);

        assertThat(response.accessToken()).isEqualTo("access.token.value");
        assertThat(response.tokenType()).isEqualTo("Bearer");
        assertThat(response.nickname()).isEqualTo(NICKNAME);
        assertThat(response.role()).isEqualTo(MemberRole.USER);
    }

    @Test
    @DisplayName("비밀번호가 틀리면 로그인에 실패한다")
    void login_wrongPassword_throws() {
        LoginRequest request = new LoginRequest(EMAIL, "wrongPassword");
        Member member = member();

        given(memberRepository.findByEmail(EMAIL)).willReturn(Optional.of(member));
        given(passwordEncoder.matches("wrongPassword", ENCODED_PASSWORD)).willReturn(false);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_CREDENTIALS);
    }

    @Test
    @DisplayName("존재하지 않는 이메일로 로그인하면 실패한다")
    void login_unknownEmail_throws() {
        LoginRequest request = new LoginRequest(EMAIL, RAW_PASSWORD);
        given(memberRepository.findByEmail(EMAIL)).willReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_CREDENTIALS);
    }

    private Member member() {
        return Member.builder()
                .email(EMAIL)
                .password(ENCODED_PASSWORD)
                .nickname(NICKNAME)
                .role(MemberRole.USER)
                .build();
    }
}
