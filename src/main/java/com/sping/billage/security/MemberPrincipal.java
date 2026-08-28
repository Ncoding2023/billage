package com.sping.billage.security;

import com.sping.billage.domain.member.enums.MemberRole;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * 인증된 사용자 정보. 컨트롤러에서 @AuthenticationPrincipal 로 주입받는다.
 */
@Getter
public class MemberPrincipal implements UserDetails {

    private final Long memberId;
    private final String email;
    private final MemberRole role;

    public MemberPrincipal(Long memberId, String email, MemberRole role) {
        this.memberId = memberId;
        this.email = email;
        this.role = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return email;
    }
}
